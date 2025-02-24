package com.example.Sparta_Store.payment.entity;

import com.example.Sparta_Store.orders.entity.Orders;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "amount", nullable = false, updatable = false)
    private Long amount;

    @Column(name = "method")
    private String method;

    @Column(name = "is_cancelled")
    private boolean isCancelled;

    @Column(name = "is_aborted")
    private boolean isAborted;

    public Payment(
        String paymentKey,
        Orders order,
        Long amount
    ) {
        this.paymentKey = paymentKey;
        this.order = order;
        this.amount = amount;
        this.isCancelled = false;
        this.isAborted = false;
    }

    public void updateCancelled() {
        this.isCancelled = true;
    }

    public void updateAborted() {
        this.isAborted = true;
    }

    public void approvedPayment(String date, String method) {
        OffsetDateTime odt = OffsetDateTime.parse(date);
        this.approvedAt = odt.toLocalDateTime();
        this.method = method;
    }
}
