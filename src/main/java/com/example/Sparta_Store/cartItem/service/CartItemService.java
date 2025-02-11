package com.example.Sparta_Store.cartItem.service;

import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.cartItem.repository.CartItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    // 카트 초기화
    public void deleteCartItem(List<CartItem> cartItemList) {

        for(CartItem cartItem:cartItemList){
            cartItemRepository.delete(cartItem);
        }

    }

}
