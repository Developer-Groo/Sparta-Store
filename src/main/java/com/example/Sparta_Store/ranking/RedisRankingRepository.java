package com.example.Sparta_Store.ranking;

import com.example.Sparta_Store.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RedisRankingRepository extends JpaRepository<Item,Long> {

    @Query("SELECT c.name FROM Item i JOIN i.category c WHERE i.id = :itemId")
    String findCategoryNameByItemId(@Param("itemId") Long itemId);

    @Query("SELECT i.name FROM Item i WHERE i.id = :itemId")
    String findItemNameByItemId(@Param("itemId") Long itemId);
}
