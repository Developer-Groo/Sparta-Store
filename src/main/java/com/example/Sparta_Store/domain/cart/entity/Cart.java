package com.example.Sparta_Store.domain.cart.entity;

import com.example.Sparta_Store.domain.cartItem.entity.CartItem;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.domain.user.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cart extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<Long, CartItem> cartItems = new HashMap<>();

    public Cart(Users user) {
        this.user = user;
    }

    public void addCartItem(Long userId, CartItem cartItem) {
        cartItems.put(userId, cartItem);
    }

    @JsonIgnore
    public Collection<CartItem> getCartItems () {
        return this.cartItems.values();
    }


    public void removeCartItem(Long itemId) {
        cartItems.remove(itemId);
    }
}
