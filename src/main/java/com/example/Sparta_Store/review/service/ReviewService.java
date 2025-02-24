package com.example.Sparta_Store.review.service;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.orderItem.repository.OrderItemRepository;
import com.example.Sparta_Store.orders.OrderStatus;
import com.example.Sparta_Store.review.dto.response.ReviewResponseDto;
import com.example.Sparta_Store.review.entity.Review;
import com.example.Sparta_Store.review.exception.ReviewErrorCode;
import com.example.Sparta_Store.review.repository.ReviewRepository;
import com.example.Sparta_Store.user.entity.Users;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    public PageResult<ReviewResponseDto> getReviews(Long itemId, PageQuery pageQuery) {
        Page<ReviewResponseDto> reviewList = reviewRepository.findByItemId(itemId, pageQuery.toPageable())
                .map(ReviewResponseDto::toDto);

        return PageResult.from(reviewList);
    }

    @Transactional
    public ReviewResponseDto createReview(
            Long userId,
            Long itemId,
            String content,
            String imgUrl,
            int rating
    ) {
        OrderItem orderItem = orderItemRepository.findOrderItemWithUserAndItem(userId, itemId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_PURCHASED));

        if (orderItem.getOrders().getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new CustomException(ReviewErrorCode.REVIEW_NOT_CONFIRMED);
        }

        Users user = orderItem.getOrders().getUser();
        Item item = orderItem.getItem();

        Review savedReview = reviewRepository.save(
                Review.toEntity(user, item, content, imgUrl, rating)
        );
        return ReviewResponseDto.toDto(savedReview);
    }

    @Transactional
    public ReviewResponseDto updateReview(
            Long requestUserId,
            Long reviewId,
            String content,
            String imgUrl,
            int rating
    ) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.checkOwnership(requestUserId);
        review.updateReview(content, imgUrl, rating);
        return ReviewResponseDto.toDto(review);
    }

    @Transactional
    public void deleteReview(Long requestUserId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.checkOwnership(requestUserId);
        reviewRepository.delete(review);
    }
}
