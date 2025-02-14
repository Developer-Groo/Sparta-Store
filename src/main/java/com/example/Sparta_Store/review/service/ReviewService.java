package com.example.Sparta_Store.review.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.review.dto.response.ReviewResponseDto;
import com.example.Sparta_Store.review.entity.Review;
import com.example.Sparta_Store.review.repository.ReviewRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
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
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

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
            String imgUrl
    ) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Review savedReview = reviewRepository.save(Review.toEntity(user, item, content, imgUrl));
        return ReviewResponseDto.toDto(savedReview);
    }

    @Transactional
    public ReviewResponseDto updateReview(
            Long requestUserId,
            Long reviewId,
            String content,
            String imgUrl
    ) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰는 존재하지 않습니다."));

        review.checkOwnership(requestUserId);

        review.updateReview(content);
        review.updateImageUrl(imgUrl);
        return ReviewResponseDto.toDto(review);
    }

    @Transactional
    public void deleteReview(Long requestUserId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰는 존재하지 않습니다."));

        review.checkOwnership(requestUserId);
        reviewRepository.delete(review);
    }
}
