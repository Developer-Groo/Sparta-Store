package com.example.Sparta_Store.point.entity;

import com.example.Sparta_Store.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "point_balance", nullable = false)
    private int balance;  // 현재 포인트 잔액

    public Point(User user, int balance) {
        this.user = user;
        this.balance = balance;
    }

    public void addPoints(int amount) {
        this.balance += amount;
    }

    public void usePoints(int amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.balance -= amount;
    }

}
