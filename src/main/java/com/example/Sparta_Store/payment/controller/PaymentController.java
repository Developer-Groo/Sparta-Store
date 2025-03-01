package com.example.Sparta_Store.payment.controller;

import com.example.Sparta_Store.admin.orders.service.AdminOrderService;
import com.example.Sparta_Store.orders.service.OrderService;
import com.example.Sparta_Store.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
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

    @Value("${TOSS_CLIENT_KEY}")
    private String clientKey;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final AdminOrderService adminOrderService;

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

        // 결제 정보 불러오기
        try{
            orderService.getPaymentInfo(model, userId, orderId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            return "redirect:/fail.html?message=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        }

        // 상태가 BEFORE_PAYMENT 인 주문인지 확인
        if (!paymentService.checkBeforePayment(orderId)) {
            log.info("BEFORE_PAYMENT 상태인 주문만 결제가 가능합니다.");
            String errorMessage = "결제가 완료된 주문이거나, 결제를 할 수 없는 주문입니다.";
            return "redirect:/fail.html?message=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        }
        model.addAttribute("clientKey", clientKey);

        return "payment/checkout";
    }

    /**
     * 결제 승인
     */
    @PostMapping(value = { "/confirm"})
    public ResponseEntity<JSONObject> confirmPayment(HttpServletRequest request, @RequestBody String jsonBody) throws Exception {

        log.info("결제 승인 요청됨 >> {}", jsonBody);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonBody);

        JSONObject response;

        Long userId = (Long) request.getAttribute("id");
        String orderId = (String) jsonObject.get("orderId");

        try{
            response = paymentService.confirmPayment(userId, jsonBody);
        } catch (Exception e) {
            log.error("결제 승인 에러 발생: {}", e.getMessage());
            adminOrderService.orderCancelled(orderId);

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(jsonResponse);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}
