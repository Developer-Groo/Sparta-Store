package com.example.Sparta_Store.couponUser.entity;

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
public class CouponUser extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_user_id", nullable = false)
    private Long id;

    @Column(name = "coupon_name", nullable = false)
    private String name; // 쿠폰명

    @Column(name = "content", nullable = false)
    private String content; // 10%

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_used")
    private boolean isUsed;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    private CouponUser(
        String name,
        String content,
        Long userId,
        LocalDateTime expirationDate
    ) {
        this.name = name;
        this.content = content;
        this.userId = userId;
        this.isUsed = false;
        this.expirationDate = expirationDate;
    }

    public static CouponUser toEntity(
        String name,
        String content,
        Long userId,
        LocalDateTime expirationDate
    ) {
        return new CouponUser(
            name,
            content,
            userId,
            expirationDate
        );
    }
}
