package com.example.Sparta_Store.address.entity;

public record AddressDto(String city, String street, String zipcode) {
    public Address toEntity() {
        return new Address(city, street, zipcode);
    }
}
