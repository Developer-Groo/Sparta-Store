package com.example.Sparta_Store.domain.category.service;

import com.example.Sparta_Store.domain.category.dto.response.CategoryResponseDto;
import com.example.Sparta_Store.domain.category.repository.CategoryRepository;
import com.example.Sparta_Store.domain.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public List<CategoryResponseDto> getCategoryTree() {
        return categoryRepository.getCategoryTree().stream()
                .map(CategoryResponseDto::toDto)
                .toList();
    }

    /**
     * 특정 카테고리의 상품목록 조회
     */
    public PageResult<ItemResponseDto> getItemsByCategory(Long categoryId, boolean inStock, Pageable pageable) {
        Page<ItemResponseDto> findItems = itemRepository.findByCategoryId(categoryId, inStock, pageable)
                .map(ItemResponseDto::toDto);

        return PageResult.from(findItems);
    }
}
