package com.example.Sparta_Store.review.controller;

import com.example.Sparta_Store.review.dto.request.ReviewRequestDto;
import com.example.Sparta_Store.review.dto.response.ReviewResponseDto;
import com.example.Sparta_Store.review.service.ReviewService;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items/{itemId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PageResult<ReviewResponseDto>> getReviews(@PathVariable("itemId") Long itemId, PageQuery pageQuery) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.getReviews(itemId, pageQuery));
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable("itemId") Long itemId, ReviewRequestDto dto, HttpRequest request) {
        Long userId = (Long) request.getAttributes().get("id");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        reviewService.createReview(
                                userId,
                                itemId,
                                dto.content(),
                                dto.imgUrl(),
                                dto.rating()
                        )
                );
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("reviewId") Long reviewId, ReviewRequestDto dto, HttpRequest request) {
        Long userId = (Long) request.getAttributes().get("id");
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviewService.updateReview(userId, reviewId, dto.content(), dto.imgUrl(), dto.rating()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId, HttpRequest request) {
        Long userId = (Long) request.getAttributes().get("id");
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
