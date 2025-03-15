package com.example.Sparta_Store.domain.orders.entity;

import com.example.Sparta_Store.domain.IssuedCoupon.entity.IssuedCoupon;
import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.domain.orders.OrderStatus;
import com.example.Sparta_Store.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Orders extends TimestampedEntity {

    @Id
    @Column(name = "order_id", updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "total_price", updatable = false)
    private Long totalPrice;

    @Column(name = "address", nullable = false)
    private Address address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_coupon_id")
    private IssuedCoupon issuedCoupon;

    private Orders(
        Users user,
        long totalPrice,
        Address address
    ) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.orderStatus = OrderStatus.BEFORE_PAYMENT;
        this.totalPrice = totalPrice;
        this.address = address;
    }

    private Orders(
        Users user,
        long totalPrice,
        Address address,
        IssuedCoupon issuedCoupon
    ) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.orderStatus = OrderStatus.BEFORE_PAYMENT;
        this.totalPrice = totalPrice;
        this.address = address;
        this.issuedCoupon = issuedCoupon;
    }

    public static Orders createOrderWithoutCoupon(
        Users user,
        long totalPrice,
        Address address
    ) {
        return new Orders(
            user,
            totalPrice,
            address,
            null
        );
    }

    public static Orders createOrderWithCoupon(
        Users user,
        long totalPrice,
        Address address,
        IssuedCoupon issuedCoupon
    ) {
        return new Orders(
            user,
            totalPrice,
            address,
            issuedCoupon
        );
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

}
