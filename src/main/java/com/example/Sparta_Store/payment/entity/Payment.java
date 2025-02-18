package com.example.Sparta_Store.payment.entity;

import com.example.Sparta_Store.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_key", referencedColumnName = "customerKey",
        insertable = false, nullable = false)
    private User user;

    @Column(name = "order_number", unique = true, updatable = false, nullable = false)
    private String orderNumber;

    @Column(name = "approved_at", nullable = false, updatable = false)
    private LocalDateTime approvedAt;

    @Column(name = "method", nullable = false)
    private String method;

    public Payment(
        String paymentKey,
        User user,
        String orderNumber,
        LocalDateTime approvedAt,
        String method
    ) {
        this.paymentKey = paymentKey;
        this.user = user;
        this.orderNumber = orderNumber;
        this.approvedAt = approvedAt;
        this.method = method;
    }
}
