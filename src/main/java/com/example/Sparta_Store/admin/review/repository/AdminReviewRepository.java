package com.example.Sparta_Store.admin.review.repository;

import com.example.Sparta_Store.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminReviewRepository extends JpaRepository<Review, Long> {

    Optional<List<Review>> findReviewByItemId(Long itemId);
}
