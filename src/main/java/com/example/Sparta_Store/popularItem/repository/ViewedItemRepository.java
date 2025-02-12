package com.example.Sparta_Store.popularItem.repository;

import com.example.Sparta_Store.popularItem.entity.ViewedItem;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ViewedItemRepository extends JpaRepository<ViewedItem, Long> {

    @Query("SELECT v.item.id, COUNT(v.id) as total_views " +
            "FROM ViewedItem v " +
            "WHERE v.viewedAt >= :startDate " +
            "GROUP BY v.item.id " +
            "ORDER BY total_views DESC")
    List<Object[]> findTopViewedProducts(@Param("startDate") LocalDateTime startDate);
}
