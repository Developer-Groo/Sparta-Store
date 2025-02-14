package com.example.Sparta_Store.popularItem.repository.sold;

import com.example.Sparta_Store.popularItem.entity.SoldItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoldItemRepository extends JpaRepository<SoldItem, Long>, SoldItemRepositoryCustom {

}
