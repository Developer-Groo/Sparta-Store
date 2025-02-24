package com.example.Sparta_Store.review.service;

import com.example.Sparta_Store.oAuth.jwt.UserRoleEnum;
import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.orders.entity.Orders;
import com.example.Sparta_Store.review.dto.response.ReviewResponseDto;
import com.example.Sparta_Store.review.entity.Review;
import com.example.Sparta_Store.review.exception.ReviewErrorCode;
import com.example.Sparta_Store.review.repository.ReviewRepository;
import com.example.Sparta_Store.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private User user;
    private Item item;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        user = new User(1L,"","", "test@example", "key", null, false, "providerId", "provider", UserRoleEnum.USER);
        item = new Item(1L, "testName", "www.example.com", 10000, "test", 100, null, null);

        Orders order = new Orders("1", user, OrderStatus.CONFIRMED, 100000L, null);
        orderItem = new OrderItem(1L, order, item, 10000, 10);
    }

    @Test
    @DisplayName("리뷰 생성 성공 - 구매 확정된 상품 리뷰")
    void createReview_Success() {
        // given
        given(orderItemRepository.findOrderItemWithUserAndItem(1L, 1L))
                .willReturn(Optional.of(orderItem));

        given(reviewRepository.save(any(Review.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ReviewResponseDto response = reviewService.createReview(1L, 1L, "content", "img.png", 5);

        // then
        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo("content");
        assertThat(response.imgUrl()).isEqualTo("img.png");

        then(orderItemRepository).should().findOrderItemWithUserAndItem(1L, 1L);
        then(reviewRepository).should().save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 구매하지 않은 상품 리뷰")
    void createReview_Fail_NotPurchased() {
        // given
        given(orderItemRepository.findOrderItemWithUserAndItem(1L, 1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(1L, 1L, "content", "img.png", 4))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.REVIEW_NOT_PURCHASED);

        then(orderItemRepository).should().findOrderItemWithUserAndItem(1L, 1L);
        then(reviewRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 구매 확정되지 않은 상품")
    void createReview_Fail_NotConfirmed() {
        // given
        Orders notConfirmedOrder = new Orders("2L", user, OrderStatus.SHIPPING, 100000L, null);
        OrderItem notConfirmedOrderItem = new OrderItem(2L, notConfirmedOrder, item, 10000, 10);

        given(orderItemRepository.findOrderItemWithUserAndItem(2L, 2L))
                .willReturn(Optional.of(notConfirmedOrderItem));

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(2L, 2L, "content", "img.png", 4))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.REVIEW_NOT_CONFIRMED);

        then(orderItemRepository).should().findOrderItemWithUserAndItem(2L, 2L);
        then(reviewRepository).shouldHaveNoInteractions();
    }
}