package com.example.Sparta_Store.payment.service;

import com.example.Sparta_Store.admin.orders.service.AdminOrderService;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.payment.entity.Payment;
import com.example.Sparta_Store.payment.exception.PaymentErrorCode;
import com.example.Sparta_Store.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PaymentService")
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final OrdersRepository ordersRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AdminOrderService adminOrderService;

    @Value("${TOSS_SECRET_KEY}")
    private String secretKey;

    // 결제전, 주문상태 확인
    public boolean checkBeforePayment(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new CustomException(PaymentErrorCode.NOT_EXISTS_ORDER)
        );
        return order.getOrderStatus().equals(OrderStatus.BEFORE_PAYMENT);
    }

    // 결제 승인
    @Transactional
    public JSONObject confirmPayment(Long userId, String jsonBody) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonBody);

        String paymentKey = (String) jsonObject.get("paymentKey");
        String orderId = (String) jsonObject.get("orderId");
        long amount = Long.parseLong((String) jsonObject.get("amount"));

        // 데이터 검증
        checkData(userId, orderId, amount);

        if (!checkBeforePayment(orderId)) {
            throw new CustomException(PaymentErrorCode.MUST_BE_BEFORE_PAYMENT);
        }

        // 상품 재고 감소 및 주문 CONFIRMED 상태 변경
        orderService.checkoutOrder(orderId); //

        // Payment 엔티티 생성
        createPayment(jsonObject);

        // 결제 승인 API 호출
        log.info("결제 승인 API 호출");
        JSONObject response = confirmPaymentTossAPI(secretKey, jsonBody);

        if(response.containsKey("error")) { // 승인 실패 CASE
            log.info("결제 승인 API 에러 발생 code: {} message{}", response.get("code"), response.get("message"));
            adminOrderService.orderCancelled(orderId);
            updateAborted(paymentKey);

            throw new RuntimeException(response.get("message").toString());
        }

        return response;
    }

    // Payment 엔티티 생성
    @Transactional
    public void createPayment(JSONObject response) {

        String orderId = response.get("orderId").toString();
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new CustomException(PaymentErrorCode.NOT_EXISTS_ORDER)
        );

        Payment savedPayment = new Payment(
            response.get("paymentKey").toString(),
            order,
            Long.valueOf(response.get("amount").toString())
        );

        paymentRepository.save(savedPayment);
        log.info("Payment 엔티티 생성 완료");
    }

    // user, orderId, amount 일치 검증
    public void checkData(Long userId, String orderId, long amount) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new CustomException(PaymentErrorCode.NOT_EXISTS_ORDER)
        );
        if(!order.getUser().getId().equals(userId)) {
            throw new CustomException(PaymentErrorCode.USER_MISMATCH);
        }
        // 쿼리 파라미터의 amount 값이 메서드 파라미터로 설정한 amount와 같은지 반드시 확인
        if(order.getTotalPrice() != amount) {
            throw new CustomException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
        log.info("데이터 검증 완료");
    }

    // 승인 실패 isAborted = true
    @Transactional
    public void updateAborted(String paymentKey) {
        Payment payment = paymentRepository.findById(paymentKey).orElseThrow(
            () -> new CustomException(PaymentErrorCode.NOT_EXISTS_PAYMENT)
        );
        payment.updateAborted();
        log.info("Payment({}) isAborted = {}", payment.getPaymentKey(), payment.isAborted());
    }

    // 승인 완료 approvedAt, method 저장
    @Transactional
    public void approvedPayment(JSONObject response) {
        String paymentKey = response.get("paymentKey").toString();
        String date = response.get("approvedAt").toString();
        String method = response.get("method").toString();

        Payment payment = paymentRepository.findById(paymentKey).orElseThrow(
            () -> new CustomException(PaymentErrorCode.NOT_EXISTS_PAYMENT)
        );
        payment.approvedPayment(date, method);
        log.info("Payment({}) approvedAt = {}", payment.getPaymentKey(), payment.getApprovedAt());
    }


    // 결제 취소
    // Payment isCancelled = true
    // Order PAYMENT_CANCELLED 로 주문상태 변경
    @Transactional
    public void paymentCancelled(JSONObject response) {
        String orderId = response.get("orderId").toString();
        String paymentKey = response.get("paymentKey").toString();

        Payment payment = paymentRepository.findById(paymentKey).orElseThrow(
            () -> new CustomException(PaymentErrorCode.NOT_EXISTS_PAYMENT)
        );

        payment.updateCancelled();
        adminOrderService.orderCancelled(orderId);
    }

    /**
     * -------------- 토스 API 관련 ---------------
     */

    // 토스페이먼츠 결제 승인 API 호출
    public JSONObject confirmPaymentTossAPI(String secretKey, String jsonBody) throws Exception {
        JSONObject response = sendRequest(
            parseRequestData(jsonBody),
            secretKey,
            "https://api.tosspayments.com/v1/payments/confirm"
        );
        return response;
    }

    // 토스페이먼츠 결제 취소 API 호출
    @Transactional
    public JSONObject cancelPaymentTossAPI(String secretKey, String paymentKey, String cancleReason) throws Exception {
        JSONObject cancelRequestData = new JSONObject();
        cancelRequestData.put("cancelReason", cancleReason);

        JSONObject response = sendRequest(
            cancelRequestData,
            secretKey,
            "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"
        );
        return response;
    }

    private JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            logger.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }

    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
            Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            logger.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

}
