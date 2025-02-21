package com.example.Sparta_Store.salesSummary.entity;

import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalesSummary extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_summary_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer totalSales;

    private SalesSummary(Item item, Integer totalSales) {
        this.item = item;
        this.totalSales = totalSales;
    }

    public static SalesSummary toEntity(Item item, Integer totalSales) {
        return new SalesSummary(item, totalSales);
    }

    public void incrementSales(int quantity) {
        this.totalSales += quantity;
    }
}
