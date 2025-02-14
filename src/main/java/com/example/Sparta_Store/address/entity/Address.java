package com.example.Sparta_Store.address.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    @Column (nullable = false)
    private String city;
    @Column (nullable = false)
    private String street;
    @Column (nullable = false)
    private String zipcode;

}