package com.example.Sparta_Store.user.entity;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private boolean isDeleted;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateInformation(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public User(String email, String password ,String name, Address address){
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
    }
}
