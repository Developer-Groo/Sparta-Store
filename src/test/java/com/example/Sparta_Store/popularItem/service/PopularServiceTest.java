package com.example.Sparta_Store.popularItem.service;

import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.cart.entity.Cart;
import com.example.Sparta_Store.domain.cartItem.entity.CartItem;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.domain.likes.dto.response.LikesDto;
import com.example.Sparta_Store.domain.likes.repository.LikesRepository;
import com.example.Sparta_Store.domain.oAuth.jwt.UserRoleEnum;
import com.example.Sparta_Store.domain.popularItem.service.PopularItemService;
import com.example.Sparta_Store.domain.salesSummary.dto.SalesSummaryResponseDto;
import com.example.Sparta_Store.domain.salesSummary.entity.SalesSummary;
import com.example.Sparta_Store.domain.salesSummary.repository.SalesSummaryRepository;
import com.example.Sparta_Store.domain.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PopularItemServiceTest {

    @Mock
    private SalesSummaryRepository salesSummaryRepository;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private PopularItemService popularItemService;

    private Users user;
    private Cart cart;
    private CartItem cartItem;
    private Map<Long, CartItem> cartItemList;
    private Address address;
    private Item item1;
    private Item item2;
    private long totalPrice;
    private SalesSummary salesSummary1;
    private SalesSummary salesSummary2;

    @BeforeEach
    void setUp() {


        address = new Address("경기도", "테스트길", "12345");
        user = new Users(1L, UUID.randomUUID().toString(), "테스트유저", "email@test.com", "Pw1234!!!", address, false, null, null, UserRoleEnum.USER);
        item1 = new Item(1L, "상품1", "img1@test.com", 10000, "상품1입니다.", 100, null, null);
        item2 = new Item(2L, "상품2", "img2@test.com", 20000, "상품2입니다.", 200, null, null);
        cartItemList = new HashMap<>();
        cart = new Cart(1L, user, cartItemList);
        cartItem = new CartItem(1L, cart, item1, 2);
        cartItemList.put(item1.getId(), cartItem);

        // 최근 7일간 판매량 기록
        salesSummary1 = new SalesSummary(1L, item1, 50); // 상품1: 50개 판매
        salesSummary2 = new SalesSummary(2L, item2, 30); // 상품2: 30개 판매


    }

    @Test
    @DisplayName("최근 7일간 판매량 기준 인기 상품 조회 테스트")
    void testGetMostPopularSoldItems() {
        // Given
        given(salesSummaryRepository.findByCreatedAtAfterOrderByTotalSalesDesc(any(LocalDateTime.class)))
                .willReturn(List.of(salesSummary1, salesSummary2));

        // When
        List<SalesSummaryResponseDto> response = popularItemService.getMostPopularSoldItems();

        // Then
        assertEquals(2, response.size());

        // 첫 번째 인기 상품 검증
        assertEquals(item1.getId(), response.get(0).itemId()); // 가장 많이 팔린 상품이 item1인지 확인
        assertEquals(50, response.get(0).totalSales()); // 판매 수량 검증

        // 두 번째 인기 상품 검증
        assertEquals(item2.getId(), response.get(1).itemId()); // 두 번째 인기 상품이 item2인지 확인
        assertEquals(30, response.get(1).totalSales()); // 판매 수량 검증

        // 메서드 실행 검증
        then(salesSummaryRepository).should(times(1))
                .findByCreatedAtAfterOrderByTotalSalesDesc(any(LocalDateTime.class));
    }



    @Test
    @DisplayName("특정 상품의 좋아요 수 조회 테스트")
    void testGetLikeCount() {
        // Given
        Long itemId = 1L;
        given(likesRepository.countByItemId(itemId)).willReturn(10L);

        // When
        Long likeCount = popularItemService.getLikeCount(itemId);

        // Then
        assertEquals((10L), likeCount.longValue());
        then(likesRepository).should(times(1)).countByItemId(itemId);
    }

    @Test
    @DisplayName("좋아요 수 기준 인기 상품 조회 테스트")
    void testGetMostPopularLikedItems() {
        // Given
        given(likesRepository.findDistinctItemIds()).willReturn(List.of(1L, 2L));
        given(itemRepository.findById(1L)).willReturn(Optional.of(item1));
        given(itemRepository.findById(2L)).willReturn(Optional.of(item2));
        given(likesRepository.countByItemId(1L)).willReturn(15L);
        given(likesRepository.countByItemId(2L)).willReturn(10L);

        // When
        List<LikesDto> response = popularItemService.getMostPopularLikedItems();

        // Then
        assertEquals(2, response.size());
        assertEquals(((1L)), response.get(0).itemId().longValue()); // 가장 좋아요가 많은 상품이 1L인지 확인
        assertEquals((15L), response.get(0).totalLikes().longValue()); // 좋아요 수 확인

        then(likesRepository).should(times(1)).findDistinctItemIds();
        then(itemRepository).should(times(2)).findById(anyLong());
        then(likesRepository).should(times(2)).countByItemId(anyLong());
    }

    @Test
    @DisplayName("좋아요 상품 조회 실패 - 존재하지 않는 상품")
    void testGetMostPopularLikedItems_Fail_ItemNotFound() {
        // Given
        given(likesRepository.findDistinctItemIds()).willReturn(List.of(1L, 2L));
        given(itemRepository.findById(1L)).willReturn(Optional.of(item1));
        given(itemRepository.findById(2L)).willReturn(Optional.empty()); // 2번 상품 없음

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                popularItemService.getMostPopularLikedItems()
        );

        assertEquals("존재하지 않는 상품입니다.", exception.getMessage());
    }
}
