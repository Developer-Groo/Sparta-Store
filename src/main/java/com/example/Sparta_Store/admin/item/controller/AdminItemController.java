package com.example.Sparta_Store.admin.item.controller;

import com.example.Sparta_Store.admin.item.dto.requestDto.ItemRegisterRequestDto;
import com.example.Sparta_Store.admin.item.dto.requestDto.ItemRestockRequestDto;
import com.example.Sparta_Store.admin.item.dto.requestDto.ItemUpdateRequestDto;
import com.example.Sparta_Store.admin.item.dto.responseDto.ItemRegisterResponseDto;
import com.example.Sparta_Store.admin.item.dto.responseDto.ItemUpdateResponseDto;
import com.example.Sparta_Store.admin.item.service.AdminItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/item")
@RequiredArgsConstructor
public class AdminItemController {

    private final AdminItemService adminItemService;

    @PostMapping
    public ResponseEntity<ItemRegisterResponseDto> registerItem(@RequestBody ItemRegisterRequestDto requestDto){
        ItemRegisterResponseDto itemRegisterResponseDto = adminItemService.registerItem(
                requestDto.name(),
                requestDto.imgUrl(),
                requestDto.price(),
                requestDto.description(),
                requestDto.stockQuantity(),
                requestDto.category()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemRegisterResponseDto);
    }

    @PatchMapping("/updateItem/{id}")
    public ResponseEntity<ItemUpdateResponseDto> updateItem(@PathVariable Long id, ItemUpdateRequestDto requestDto) {
        ItemUpdateResponseDto updateResponseDto = adminItemService.updateItem(id,requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(updateResponseDto);
    }

    @PostMapping("/update/{id}/quantity")
    public ResponseEntity<ItemUpdateResponseDto> restockItem(@PathVariable("id") Long id, @RequestBody ItemRestockRequestDto dto) {
        log.info("컨트롤러");
        return ResponseEntity.status(HttpStatus.OK)
                .body(adminItemService.restockItem(id, dto.quantity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        adminItemService.deleteItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
