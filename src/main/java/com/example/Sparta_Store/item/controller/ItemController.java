package com.example.Sparta_Store.item.controller;

import com.example.Sparta_Store.item.dto.request.ItemSearchRequestDto;
import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<PageResult<ItemResponseDto>> getItems(PageQuery pageQuery) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getItems(pageQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResult<ItemResponseDto>> getSearchItems(ItemSearchRequestDto dto, PageQuery pageQuery) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getSearchItems(dto.keyword(), pageQuery));
    }
}
