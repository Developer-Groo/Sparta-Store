package com.example.Sparta_Store.category.controller;

import com.example.Sparta_Store.category.dto.response.CategoryResponseDto;
import com.example.Sparta_Store.category.service.CategoryService;
import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 전체 카테고리 조회
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getCategoryTree());
    }

    /**
     * 특정 카테고리의 상품 조회
     */
    @GetMapping("/{categoryId}/items")
    public ResponseEntity<PageResult<ItemResponseDto>> getItemsByCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "inStock", required = false, defaultValue = "false") boolean inStock,
            PageQuery pageQuery
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.getItemsByCategory(categoryId, inStock, pageQuery.toPageable()));
    }
}
