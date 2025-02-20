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

@Slf4j(topic = "PaymentController")
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

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
            log.info("BEFORE_PAYMENT 상태인 주문만 결제가 가능합니다." );
            //TOdo
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
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {
        Long userId = (Long) request.getAttribute("id");

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonBody);

        String orderId = (String) jsonObject.get("orderId");
        long amount = Long.parseLong((String) jsonObject.get("amount"));

        // 데이터 검증 프로세스
        try {
            // user, orderId, amount 일치 검증
            paymentService.checkData(userId, orderId, amount);
        } catch (Exception e) {
            paymentService.paymentCancelled(orderId);
            log.info("결제 승인 API 호출 전, 에러 발생: {}", e);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("message", "결제 승인 에러 발생");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResponse);
        }

        // 결제 승인 API 호출
        log.info("결제 승인 API 호출");
        JSONObject response = paymentService.confirmPaymentTossAPI(SECRET_KEY, jsonBody);

        if(response.containsKey("error")) {
            paymentService.paymentCancelled(orderId);
            log.info("결제 승인 API 에러 발생");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // 결제 승인 후, 상품 재고 감소 및 order 상태 변경, Payment 엔티티 생성
        try {
            paymentService.approvePayment(response);
            log.info("결제 승인 후 Payment 엔티티 생성 및 재고 감소 완료");
        } catch (Exception e) {
            log.info("결제 승인 후 Payment 엔티티 생성 및 재고 감소: {}", e);
            paymentService.paymentCancelled(orderId);
            // 결제 취소 API 호출
            log.info("결제 취소 API 호출");
            paymentService.cancelPaymentTossAPI(
                SECRET_KEY,
                response.get("paymentKey").toString(),
                ""
            );

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("message", "payment 엔티티 생성 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonResponse);
        }

        // CartItem 초기화 TODO 이벤트리스너
        cartService.deleteCartItem(userId);
        log.info("CartItem 초기화 완료");
        log.info("결제 승인 완료");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
