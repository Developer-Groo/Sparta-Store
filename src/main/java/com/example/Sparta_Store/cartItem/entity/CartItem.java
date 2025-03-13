package com.example.Sparta_Store.cartItem.entity;

import com.example.Sparta_Store.cart.entity.Cart;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.item.entity.Item;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartItem extends TimestampedEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer quantity;

    public CartItem(Cart cart, Item item, Integer quantity) {
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
