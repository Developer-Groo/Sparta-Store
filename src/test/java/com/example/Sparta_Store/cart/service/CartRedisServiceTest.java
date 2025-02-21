package com.example.Sparta_Store.cart.service;

import com.example.Sparta_Store.cartItem.entity.CartItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartRedisServiceTest {

//    @Autowired
//    private CartRedisService cartRedisService;

    @Test
    @DisplayName("수량이 잘 업데이트된다.")
    void updateQuantityTest() {
        // given
        CartItem cartItem = new CartItem(null, null, 10);

        // when
        cartItem.updateQuantity(5);

        // than
        Assertions.assertThat(cartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("장바구니에서 상품 목록을 조회하면 올바른 개수가 반환된다.")
    void getCartItemsTest() {

    }

}
