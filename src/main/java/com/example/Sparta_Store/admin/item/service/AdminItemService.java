package com.example.Sparta_Store.admin.item.service;

import com.example.Sparta_Store.admin.item.dto.requestDto.ItemUpdateRequestDto;
import com.example.Sparta_Store.admin.item.dto.responseDto.ItemRegisterResponseDto;
import com.example.Sparta_Store.admin.item.dto.responseDto.ItemUpdateResponseDto;
import com.example.Sparta_Store.admin.item.respository.AdminItemRepository;
import com.example.Sparta_Store.item.entity.Item;
import jakarta.transaction.Transactional;
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

    @Transactional
    public ItemUpdateResponseDto updateItem(Long id, ItemUpdateRequestDto requestDto) {
        Item item = findById(id);

        item.updateItem(
                requestDto.name(),
                requestDto.imgUrl(),
                requestDto.price(),
                requestDto.description(),
                requestDto.stockQuantity()
        );

        return new ItemUpdateResponseDto(
                item.getName(),
                item.getImgUrl(),
                item.getPrice(),
                item.getDescription(),
                item.getStockQuantity()
        );
    }

    private Item findById(Long id) {
        Item item = adminItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지않는 아이템입니다."));

        return item;
    }
}