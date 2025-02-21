package com.example.Sparta_Store.payment.entity;

import com.example.Sparta_Store.orders.entity.Orders;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @Column(name = "payment_key", updatable = false)
    private String paymentKey;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Orders order;

    @Column(name = "approved_at", nullable = false, updatable = false)
    private LocalDateTime approvedAt;

    @Column(name = "method", nullable = false)
    private String method;

    public Payment(
        String paymentKey,
        Orders order,
        LocalDateTime approvedAt,
        String method
    ) {
        this.paymentKey = paymentKey;
        this.order = order;
        this.approvedAt = approvedAt;
        this.method = method;
    }
}
