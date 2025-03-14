package com.example.Sparta_Store.domain.review.repository;

import com.example.Sparta_Store.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByItemId(Long itemId, Pageable pageable);
}
