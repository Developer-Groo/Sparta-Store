package com.example.Sparta_Store.admin.item.service;

import com.example.Sparta_Store.admin.item.dto.responseDto.ItemRegisterResponseDto;
import com.example.Sparta_Store.admin.item.respository.AdminItemRepository;
import com.example.Sparta_Store.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminItemService {

    private final AdminItemRepository adminItemRepository;

    public ItemRegisterResponseDto registerItem(
            String name,
            String imageUrl,
            int price,
            String description,
            int stockQuantity
    ) {
        Item item = new Item(
                name,
                imageUrl,
                price,
                description,
                stockQuantity
        );

        Item registerItem = adminItemRepository.save(item);

        return ItemRegisterResponseDto.toDto(registerItem);
    }
}
