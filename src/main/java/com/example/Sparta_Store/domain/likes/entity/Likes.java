package com.example.Sparta_Store.domain.likes.entity;

import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Likes extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int likeCount;

    public Likes(Users user, Item item) {
        this.user = user;
        this.item = item;
        this.likeCount = 1;
    }

}
