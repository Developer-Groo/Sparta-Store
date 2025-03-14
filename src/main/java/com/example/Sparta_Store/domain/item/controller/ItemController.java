package com.example.Sparta_Store.domain.item.controller;

import com.example.Sparta_Store.domain.item.dto.request.ItemSearchRequestDto;
import com.example.Sparta_Store.domain.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.domain.item.dto.response.SelectItemResponseDto;
import com.example.Sparta_Store.domain.item.service.ItemService;
import com.example.Sparta_Store.domain.item.ranking.service.RedisRankingService;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final RedisRankingService redisRankingService;

    @GetMapping
    public ResponseEntity<PageResult<ItemResponseDto>> getItems(
            @RequestParam(value = "inStock", required = false, defaultValue = "false") boolean inStock,
            PageQuery pageQuery
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getItems(inStock, pageQuery));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResult<ItemResponseDto>> getSearchItems(
            @RequestParam(value = "inStock", required = false, defaultValue = "false") boolean inStock,
            ItemSearchRequestDto dto, PageQuery pageQuery
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getSearchItems(inStock, dto.keyword(), pageQuery));
    }

    @GetMapping("/select/{id}")
    public ResponseEntity<SelectItemResponseDto> selectItem(@PathVariable Long id){
        SelectItemResponseDto selectItemResponseDto = itemService.SelectItem(id);
        redisRankingService.addToRedis(id);
        return new ResponseEntity<>(selectItemResponseDto,HttpStatus.OK);
    }
}
