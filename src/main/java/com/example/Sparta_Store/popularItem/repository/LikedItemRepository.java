package com.example.Sparta_Store.popularItem.repository;

import com.example.Sparta_Store.popularItem.entity.LikedItem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface LikedItemRepository extends JpaRepository<LikedItem, Long> {
    @Query("SELECT l.item.id, COUNT(l) as like_count " +
            "FROM LikedItem l " +
            "GROUP BY l.item.id " +
            "ORDER BY like_count DESC")
    List<Object[]> findMostLikedItems();
}
