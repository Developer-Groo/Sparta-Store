package com.example.Sparta_Store.user.entity;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.common.entity.TimestampedEntity;
import com.example.Sparta_Store.oAuth.jwt.UserRoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users extends TimestampedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "customer_key", unique = true, updatable = false)
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
    private Boolean isDeleted;

    @Column(unique = true)
    private String providerId;

    @Column
    private String provider;

    @Column
    private UserRoleEnum role;

    public Users(String email, String password, String name, Address address) {
        this.isDeleted = false;
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.customerKey = UUID.randomUUID().toString();
        this.role = UserRoleEnum.USER;
    }

    public Users(String provider, String providerId , String name, String email, Address address , UserRoleEnum role){
        this.isDeleted = false;
        this.provider = provider;
        this.providerId = providerId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.customerKey = UUID.randomUUID().toString();
        this.role = role;
    }

    public Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Users(String email, String password, String name, Address address,UserRoleEnum role) {
        this.isDeleted = false;
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.customerKey = UUID.randomUUID().toString();
        this.role = role;
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
