package com.example.Sparta_Store.IssuedCoupon.entity;

import com.example.Sparta_Store.common.entity.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IssuedCoupon extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issued_coupon_id", nullable = false)
    private Long id;

    @Column(name = "coupon_name", nullable = false)
    private String name; // 쿠폰명

    @Column(name = "amount", nullable = false)
    private String amount; // 쿠폰 금액

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_used")
    private boolean isUsed;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    private IssuedCoupon(
        String name,
        String content,
        Long userId,
        LocalDateTime expirationDate
    ) {
        this.name = name;
        this.amount = content;
        this.userId = userId;
        this.isUsed = false;
        this.expirationDate = expirationDate;
    }

    public static IssuedCoupon toEntity(
        String name,
        String content,
        Long userId,
        LocalDateTime expirationDate
    ) {
        return new IssuedCoupon(
            name,
            content,
            userId,
            expirationDate
        );
    }

    public void updateIsUsed() {
        this.isUsed = true;
    }
}
