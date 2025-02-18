package com.example.Sparta_Store.item.entity;

import com.example.Sparta_Store.category.entity.Category;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.salesSummary.entity.SalesSummary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String imgUrl;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToOne(mappedBy = "item") // mappedBy 를 설정할 경우 자동 Lazy 적용
    private SalesSummary salesSummary;

    private Item(
            String name,
            String imgUrl,
            int price,
            String description,
            int stockQuantity,
            Category category
    ) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public static Item toEntity(
            String name,
            String imgUrl,
            int price,
            String description,
            int stockQuantity,
            Category category
    ) {
        return new Item(
                name,
                imgUrl,
                price,
                description,
                stockQuantity,
                category
        );
    }

    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }

    public void updateItem(
            String name,
            String imgUrl,
            Integer price,
            String description,
            Integer stockQuantity,
            Category category) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (imgUrl != null && !imgUrl.isEmpty()) {
            this.imgUrl = imgUrl;
        }
        if (price != null) {
            this.price = price;
        }
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
        if (stockQuantity != null) {
            this.stockQuantity = stockQuantity;
        }
        if (category != null) {
            this.category = category;
        }
    }

    public int getTotalSales() {
        return salesSummary != null ? salesSummary.getTotalSales() : 0;
    }
}