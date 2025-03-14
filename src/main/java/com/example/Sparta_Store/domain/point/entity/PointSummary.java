package com.example.Sparta_Store.domain.point.entity;

import com.example.Sparta_Store.domain.point.SummaryType;
import com.example.Sparta_Store.domain.user.entity.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "point_amount", nullable = false)
    private Long amount;  // 변동된 포인트 값

    @Enumerated(EnumType.STRING)
    @Column(name = "summary_type", nullable = false)
    private SummaryType SummaryType;  // 적립 또는 사용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PointSummary(Users user, Long amount, SummaryType summaryType) {
        this.user = user;
        this.amount = amount;
        this.SummaryType = summaryType;
        this.createdAt = LocalDateTime.now();

    }
}
