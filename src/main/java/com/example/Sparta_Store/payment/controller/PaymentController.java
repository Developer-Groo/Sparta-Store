package com.example.Sparta_Store.payment.controller;

import com.example.Sparta_Store.cart.service.CartService;
import com.example.Sparta_Store.orders.repository.OrdersRepository;
import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j(topic = "PaymentController")
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${TOSS_SECRET_KEY}")
    private String SECRET_KEY;
    @Value("${TOSS_CLIENT_KEY}")
    private String CLIENT_KEY;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final OrdersRepository ordersRepository;
    private final CartService cartService;

    /**
     * 결제창 생성
     */
    @GetMapping("/checkout/{orderId}")
    public String checkoutPay(
        HttpServletRequest request,
        @PathVariable("orderId") String orderId,
        Model model
    ) {
        Long userId = (Long) request.getAttribute("id");

        // 상태가 BEFORE_PAYMENT 인 주문인지 확인
        if (!paymentService.checkStatus(orderId)) {
            return "/fail"; // "/fail" 페이지로 이동
        }

        // 결제 정보 불러오기
        orderService.getPaymentInfo(model, userId, orderId);
        model.addAttribute("clientKey", CLIENT_KEY);

        return "payment/checkout";
    }

    /**
     * 결제 승인
     */
    @PostMapping(value = { "/confirm"})
    public ResponseEntity<Map<String,String>> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {
        Long userId = (Long) request.getAttribute("id");

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonBody);

        // orderId, amount 일치 검증
        String orderId = (String) jsonObject.get("orderId");
        long amount = Long.parseLong((String) jsonObject.get("amount"));

        try {
            // user, orderId, amount 일치 검증
            paymentService.checkData(userId, orderId, amount);
        } catch (Exception e) {
            paymentService.paymentCancelled(orderId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "상품 주문에 실패하였습니다."));
        }

        // 결제 승인 API 호출
        String secretKey = SECRET_KEY;
        JSONObject response = sendRequest(
            parseRequestData(jsonBody),
            secretKey,
            "https://api.tosspayments.com/v1/payments/confirm"
        );
        log.info("결제 승인 API 호출");

        if(response.containsKey("error")) {
            paymentService.paymentCancelled(orderId);
            log.info("결제 승인 에러 발생");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "상품 주문에 실패하였습니다."));
        }

        // 결제 승인되어 Payment 엔티티 생성
        try {
            // 상품 재고 감소 및 order 상태 변경
            paymentService.checkout(orderId);
            // payment 엔티티 생성
            paymentService.createPayment(response);
        } catch (Exception e) {
            paymentService.paymentCancelled(orderId);
            log.info("Payment 엔티티 생성 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "상품 주문에 실패하였습니다."));
        }

        // CartItem 초기화 TODO 이벤트리스너
        cartService.deleteCartItem(userId);
        log.info("CartItem 초기화 완료");

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "상품 주문이 완료되었습니다."));
    }

    /**
     * 결제 실패
     */
    @GetMapping(value = "/fail")
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));

        return "/fail";
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
