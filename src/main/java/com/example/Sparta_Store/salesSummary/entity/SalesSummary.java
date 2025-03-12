package com.example.Sparta_Store.salesSummary.entity;

import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.item.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
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

    private SalesSummary(int totalSales) {
        this.totalSales = totalSales;
    }

    public static SalesSummary toEntity(int totalSales) {
        return new SalesSummary(totalSales);
    }

    public void incrementSales(int quantity) {
        this.totalSales += quantity;
    }

    public void updateItem(Item item) {
        this.item = item;
    }

}
