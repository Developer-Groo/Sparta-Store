package com.example.Sparta_Store.user.dto;

import com.example.Sparta_Store.address.entity.Address;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateInformationRequestDto {

    private final String userName;

    private final Address address;

}
