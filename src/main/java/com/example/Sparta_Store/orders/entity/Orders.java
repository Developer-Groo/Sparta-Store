package com.example.Sparta_Store.orders.entity;

import com.example.Sparta_Store.common.entity.TimestampedEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends TimestampedEntity {

}
