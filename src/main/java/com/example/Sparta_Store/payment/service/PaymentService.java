package com.example.Sparta_Store.payment.service;

import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.payment.entity.Payment;
import com.example.Sparta_Store.payment.repository.PaymentRepository;
import com.example.Sparta_Store.user.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "PaymentService")
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 결제전, 주문상태 확인
    public boolean checkStatus(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        return order.getOrderStatus().equals(OrderStatus.BEFORE_PAYMENT);
    }

    // 결제 승인되어 Payment 엔티티 생성
    @Transactional
    public void createPayment(JSONObject response) {

        String orderId = response.get("orderId").toString();
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );

        OffsetDateTime odt = OffsetDateTime.parse(response.get("approvedAt").toString());
        LocalDateTime approvedAt = odt.toLocalDateTime();

        Payment savedPayment = new Payment(
            response.get("paymentKey").toString(),
            order,
            approvedAt,
            response.get("method").toString()
        );
        paymentRepository.save(savedPayment);
    }

    // user, orderId, amount 일치 검증
    public void checkData(Long userId, String orderId, long amount) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        if(!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("유저 정보가 일치하지 않습니다.");
        }
        if(order.getTotalPrice() != amount) {
            throw new IllegalArgumentException("결제 금액이 변동되었습니다.");
        }
    }

    // 상품 재고 감소 및 order 상태 변경
    @Transactional
    public void checkout(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        // 상품 재고 감소
        List<OrderItem> orderItemList = orderItemRepository.findOrderItemsByOrders(order).orElseThrow(
            () -> new IllegalArgumentException("주문 상품 정보를 찾을 수 없습니다.")
        );
        itemService.decreaseStock(orderItemList);
        // 주문상태 변경
        order.updateOrderStatus(OrderStatus.CONFIRMED);
    }

    // PAYMENT_CANCELLED 로 주문상태 변경
    @Transactional
    public void paymentCancelled(String orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("주문 정보를 찾을 수 없습니다.")
        );
        // 주문상태 변경
        order.updateOrderStatus(OrderStatus.PAYMENT_CANCELLED);
    }

    // 토스페이먼츠 결제 승인 API 호출
    public JSONObject confirmPayment(String secretKey, String jsonBody) throws Exception {
        JSONObject response = sendRequest(
            parseRequestData(jsonBody),
            secretKey,
            "https://api.tosspayments.com/v1/payments/confirm"
        );
        return response;
    }

    // 토스페이먼츠 결제 취소 API 호출
    public JSONObject cancelPayment(String secretKey, String paymentKey, String cancleReason) throws Exception {
        JSONObject cancelRequestData = new JSONObject();
        cancelRequestData.put("cancelReason", cancleReason);

        JSONObject response = sendRequest(
            cancelRequestData,
            secretKey,
            "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"
        );
        return response;
    }

    // ------- 토스 API 관련 --------
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
