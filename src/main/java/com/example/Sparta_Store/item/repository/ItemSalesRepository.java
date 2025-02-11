package com.example.Sparta_Store.item.repository;

import com.example.Sparta_Store.item.entity.SoldItem;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//public interface ItemSalesRepository extends JpaRepository<SoldItem, Long> {
//    @Query("SELECT ps.product.id, COUNT(ps.id) as total_sales " +
//            "FROM ProductSales ps " +
//            "WHERE ps.soldAt >= :startDate " +
//            "GROUP BY ps.product.id " +
//            "ORDER BY total_sales DESC")
//    List<Object[]> findTopSellingProducts(@Param("startDate") LocalDateTime startDate);
//}

