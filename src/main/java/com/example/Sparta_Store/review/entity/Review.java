package com.example.Sparta_Store.review.entity;

import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private String content;

    private String imgUrl;

    public Review(
            User user,
            Item item,
            String content,
            String imgUrl
    ) {
        this.user = user;
        this.item = item;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public static Review toEntity(
            User user,
            Item item,
            String content,
            String imgUrl
    ) {
        return new Review(
                user,
                item,
                content,
                imgUrl != null && !imgUrl.isBlank() ? imgUrl : null
        );
    }

    /**
     * 권한 검증 메서드
     */
    public void checkOwnership(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new IllegalArgumentException("리뷰를 수정할 권한이 없습니다.");
        }
    }

    /**
     * 리뷰 내용 업데이트
     */
    public void updateReview(String content) {
        this.content = content;
    }

    /**
     * 이미지 업데이트 (Optional)
     */
    public void updateImageUrl(String newImageUrl) {
        this.imgUrl = newImageUrl;
    }
}
