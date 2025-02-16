package com.example.Sparta_Store.admin.item.service;

import com.example.Sparta_Store.admin.item.dto.requestDto.ItemUpdateRequestDto;
import com.example.Sparta_Store.admin.item.dto.responseDto.ItemRegisterResponseDto;
import com.example.Sparta_Store.admin.item.dto.responseDto.ItemUpdateResponseDto;
import com.example.Sparta_Store.admin.item.respository.AdminItemRepository;
import com.example.Sparta_Store.admin.review.service.AdminReviewService;
import com.example.Sparta_Store.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminItemService {

    private final AdminItemRepository adminItemRepository;
    private final AdminReviewService adminReviewService;

    @Transactional
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

    @Transactional
    public void deleteItem(Long itemId) {
        Item item = findById(itemId);
        adminItemRepository.delete(item);
        adminReviewService.deleteReviewByItem(itemId);
    }

    private Item findById(Long id) {
        return adminItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지않는 아이템입니다."));
    }
}