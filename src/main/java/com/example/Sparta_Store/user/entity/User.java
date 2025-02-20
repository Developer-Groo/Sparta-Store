package com.example.Sparta_Store.user.entity;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimestampedEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, updatable = false)
    private String customerKey;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(unique = true)
    private String providerId;

    @Column
    private String provider;

    public User(String email, String password ,String name, Address address){
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.customerKey = UUID.randomUUID().toString();
    }

    public User(String provider, String providerId , String name, String email, Address address){
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void updateUserInfo(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public void disableUser() {
        this.isDeleted = true;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

}
