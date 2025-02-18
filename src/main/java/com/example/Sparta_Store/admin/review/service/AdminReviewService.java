package com.example.Sparta_Store.admin.review.service;

import com.example.Sparta_Store.admin.review.repository.AdminReviewRepository;
import com.example.Sparta_Store.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final AdminReviewRepository adminReviewRepository;

    @Transactional
    public void deleteReviewByItem(Long itemId) {
        List<Review> reviews = adminReviewRepository.findReviewByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품의 리뷰가 존재하지 않습니다."));

        adminReviewRepository.deleteAll(reviews);
    }
}
