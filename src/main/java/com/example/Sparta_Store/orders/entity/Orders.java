package com.example.Sparta_Store.orders.entity;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.user.entity.Users;
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

    public Orders(Users user, long totalPrice, Address address) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.orderStatus = OrderStatus.BEFORE_PAYMENT;
        this.totalPrice = totalPrice;
        this.address = address;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

}
