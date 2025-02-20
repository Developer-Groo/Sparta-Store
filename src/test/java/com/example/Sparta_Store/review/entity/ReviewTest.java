package com.example.Sparta_Store.review.entity;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.review.exception.ReviewErrorCode;
import com.example.Sparta_Store.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewTest {

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1L, "key", "test", "test@example", "password", null, false);
        item = new Item(1L, "testItem", "img.png", 10000, "item", 100, null, null);
    }

    @Test
    @DisplayName("리뷰 생성 성공 - 이미지가 없을 경우 null 처리")
    void createReview_NoImage_Success() {
        // given & when
        Review review = Review.toEntity(user, item, "content", null, 4);

        // then
        assertThat(review.getImgUrl()).isNull();
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 별점이 1점 미만")
    void createReview_Fail_InvalidRating_Low() {
        // when & then
        assertThatThrownBy(() -> Review.toEntity(user, item, "content", "img.png", 0))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.INVALID_RATING_VALUE);
    }

    @Test
    @DisplayName("리뷰 생성 실패 - 별점이 5점 초과")
    void createReview_Fail_InvalidRating_High() {
        // when & then
        assertThatThrownBy(() -> Review.toEntity(user, item, "content", "img.png", 6))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.INVALID_RATING_VALUE);
    }

    @Test
    @DisplayName("리뷰 소유자 검증 성공")
    void checkOwnership_Success() {
        // given
        Review review = Review.toEntity(user, item, "content", "img.png", 3);

        // when & then
        assertThatCode(() -> review.checkOwnership(user.getId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("리뷰 소유자 검증 실패")
    void checkOwnership_Fail() {
        // given
        Review review = Review.toEntity(user, item, "content", "img.png", 3);

        // when & then
        assertThatThrownBy(() -> review.checkOwnership(100L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ReviewErrorCode.REVIEW_UPDATE_FORBIDDEN);
    }

}