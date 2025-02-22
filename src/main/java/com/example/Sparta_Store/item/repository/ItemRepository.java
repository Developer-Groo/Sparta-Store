package com.example.Sparta_Store.item.repository;

import com.example.Sparta_Store.item.entity.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQueryRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            "select i " +
                    "from Item i " +
                    "where i.id in :itemIds"
    )
    List<Item> findAllByIdWithLock(@Param("itemIds") List<Long> itemIds);
}
