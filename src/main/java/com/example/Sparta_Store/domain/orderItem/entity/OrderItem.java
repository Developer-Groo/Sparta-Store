package com.example.Sparta_Store.domain.orderItem.entity;

import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.orders.entity.Orders;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItem extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "order_price")
    private Integer orderPrice;

    @Column(name = "quantity")
    private Integer quantity;

    private OrderItem(
        Orders order,
        Item item,
        Integer orderPrice,
        Integer quantity
    ) {
        this.orders = order;
        this.item = item;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }

    public static OrderItem toEntity(
        Orders order,
        Item item,
        Integer orderPrice,
        Integer quantity
    ) {
        return new OrderItem(
            order,
            item,
            orderPrice,
            quantity
        );
    }
}
