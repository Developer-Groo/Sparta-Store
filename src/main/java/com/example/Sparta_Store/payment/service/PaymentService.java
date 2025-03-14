package com.example.Sparta_Store.payment.service;

import com.example.Sparta_Store.IssuedCoupon.entity.IssuedCoupon;
import com.example.Sparta_Store.IssuedCoupon.repository.IssuedCouponRepository;
import com.example.Sparta_Store.email.event.OrderStatusUpdatedEvent;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.event.OrderCancelledEvent;
import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.payment.entity.Payment;
import com.example.Sparta_Store.payment.event.PaymentApprovedEvent;
import com.example.Sparta_Store.payment.exception.PaymentErrorCode;
import com.example.Sparta_Store.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PaymentService")
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final IssuedCouponRepository issuedCouponRepository;

    @Value("${TOSS_SECRET_KEY}")
    private String secretKey;

    // 결제전, 주문상태 확인
    public boolean checkBeforePayment(String orderId) {
        Orders order = orderService.getRedisOrder(orderId);
        if (order == null) {
            throw new CustomException(PaymentErrorCode.NOT_EXISTS_ORDER);
        }

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

        Orders order = orderService.getRedisOrder(orderId);
        if (order == null) {
            throw new CustomException(PaymentErrorCode.NOT_EXISTS_ORDER);
        }

        // 데이터 검증
        checkData(userId, order, amount);
        if (!checkBeforePayment(orderId)) {
            throw new CustomException(PaymentErrorCode.MUST_BE_BEFORE_PAYMENT);
        }

        // 상태 변경
        order.updateOrderStatus(OrderStatus.ORDER_COMPLETED);

        // redis 데이터도 업데이트
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            redisTemplate.opsForHash().put("order:" + orderId, "order", orderJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Order 객체 JSON 변환 실패", e);  // 또는 커스텀 예외 던지기
        }

        // MySQL 저장
        orderService.createMysqlOrder(order);
        orderService.createMysqlOrderItem(orderId);

        // 쿠폰 사용처리
        if (order.getIssuedCoupon() != null) {
            IssuedCoupon coupon = order.getIssuedCoupon();
            coupon.updateIsUsed();
            issuedCouponRepository.save(coupon);
        }

        // 상품 재고 감소
        orderService.completeOrder(orderId);

        // Payment 엔티티 생성
        createPayment(jsonObject);

        // ----------------- 결제 승인 API 호출 --------------------
        log.info("결제 승인 API 호출");
        JSONObject response = confirmPaymentTossAPI(secretKey, jsonBody);

        // 승인 실패 CASE
        if(response.containsKey("error")) {
            log.error("결제 승인 API 에러 발생 code: {} message{}", response.get("code"), response.get("message"));
            updateAborted(paymentKey);

            throw new RuntimeException(response.get("message").toString());
        }

        // 승인 성공 CASE
        // Payment approvedAt, method 저장
        log.info("결제 승인 완료 (orderId: {})", orderId);
        try {
            approvedPayment(response);
        } catch (Exception e) {
            log.error("Payment approvedAt, method 업데이트 중, 예외 발생 (paymentKey = {})", paymentKey, e);
        }

        // CartItem 초기화 비동기 이벤트 발행
        eventPublisher.publishEvent(new PaymentApprovedEvent(userId));
        // 이메일 전송 비동기 이벤트 발행
        eventPublisher.publishEvent(OrderStatusUpdatedEvent.toEvent(order));

        return response;
    }

    // Payment 엔티티 생성
    @Transactional
    public void createPayment(JSONObject response) {

        String orderId = response.get("orderId").toString();
        Orders order = orderService.getOrder(orderId);

        Payment savedPayment = Payment.toEntity(
            response.get("paymentKey").toString(),
            order,
            Long.valueOf(response.get("amount").toString())
        );

        paymentRepository.save(savedPayment);
        log.info("Payment 엔티티 생성 완료");
    }

    // user, orderId, amount 일치 검증
    public void checkData(Long userId, Orders order, long amount) {
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
        Payment payment = getPayment(paymentKey);
        payment.updateAborted();
        log.info("Payment({}) isAborted = {}", payment.getPaymentKey(), payment.isAborted());
    }

    // 승인 완료 approvedAt, method 저장
    @Transactional
    @Retryable(
        value = { CustomException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void approvedPayment(JSONObject response) {
        String paymentKey = response.get("paymentKey").toString();
        String date = response.get("approvedAt").toString();
        String method = response.get("method").toString();

        Payment payment = getPayment(paymentKey);
        payment.approvedPayment(date, method);
        log.info("Payment 업데이트 완료 (paymentId: {}, approvedAt: {}, method: {}",
            payment.getPaymentKey(), payment.getApprovedAt(), payment.getMethod());
    }

    // 결제 취소
    // Payment isCancelled = true
    // Order PAYMENT_CANCELLED 로 주문상태 변경
    @Transactional
    public void paymentCancelled(
        String orderId,
        String paymentKey,
        String cancelReason,
        long cancelAmount
    ) throws Exception {

        Orders order = orderService.getOrder(orderId);
        Payment payment = getPayment(paymentKey);

        if (payment.isCancelled()) {
            throw new CustomException(PaymentErrorCode.ALREADY_CANCELLED);
        }

        // ----------------- 결제 취소 API 호출 --------------------
        log.info("결제 취소 API 호출");
        JSONObject response = cancelPaymentTossAPI(secretKey, paymentKey, cancelReason, cancelAmount);

        // 취소 실패 CASE
        if(response.containsKey("error")) {
            log.error("결제 취소 API 에러 발생 code: {} message{}", response.get("code"), response.get("message"));
            throw new CustomException(PaymentErrorCode.PAYMENT_CANCELLATION_FAILED);
        }

        // 취소 성공 CASE
        log.info("결제 취소 완료 (orderId: {})", orderId);

        try {
            payment.updateCancelled();
        } catch (Exception e) {
            log.error("Payment isCancelled 업데이트 중, 예외 발생 (paymentKey = {})", paymentKey, e);
        }

        // 이메일 전송 비동기 이벤트 발행
        eventPublisher.publishEvent(OrderStatusUpdatedEvent.toEvent(order));

        // 상품 재고 복구 비동기 이벤트 발행
        eventPublisher.publishEvent(OrderCancelledEvent.toEvent(order));
    }

    // 승인 취소 완료 isCancelled = true
    @Transactional
    public void updateCancelled(String paymentKey) {
        Payment payment = getPayment(paymentKey);
        payment.updateCancelled();
        log.info("Payment({}) isCancelled = {}", payment.getPaymentKey(), payment.isCancelled());
    }

    public Payment getPayment(String paymentKey) {
        return paymentRepository.findById(paymentKey).orElseThrow(
            () -> new CustomException(PaymentErrorCode.NOT_EXISTS_PAYMENT)
        );
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
    public JSONObject cancelPaymentTossAPI(
        String secretKey,
        String paymentKey,
        String cancelReason,
        long cancelAmount
    ) throws Exception {

        JSONObject cancelRequestData = new JSONObject();
        cancelRequestData.put("cancelReason", cancelReason);
        cancelRequestData.put("cancelAmount", cancelAmount);

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
