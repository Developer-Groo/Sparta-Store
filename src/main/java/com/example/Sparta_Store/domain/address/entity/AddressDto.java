package com.example.Sparta_Store.domain.address.entity;

public record AddressDto(String city, String street, String zipcode) {
    public static AddressDto toDto(String city, String street, String zipcode) {
        return new AddressDto(city, street, zipcode);
    }
}
