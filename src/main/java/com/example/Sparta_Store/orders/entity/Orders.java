package com.example.Sparta_Store.orders.entity;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends TimestampedEntity {

    @Id
    @Column(name = "order_id", updatable = false)
    private String id;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_number", unique = true, nullable = false)
//    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "total_price", updatable = false)
    private Integer totalPrice;

    @Column(name = "address", nullable = false)
    private Address address;

    public Orders(User user, int totalPrice, Address address) {
//        this.payment = payment;
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.orderStatus = OrderStatus.BEFORE_PAYMENT;
        this.totalPrice = totalPrice;
        this.address = address;
    }

//    public void setTotalPrice(int totalPrice) {
//        this.totalPrice = totalPrice;
//    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

}
