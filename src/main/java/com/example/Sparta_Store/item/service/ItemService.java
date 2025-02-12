package com.example.Sparta_Store.item.service;

import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    public PageResult<ItemResponseDto> getItems(PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = itemRepository.findAll(pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        return PageResult.from(itemList);
    }

    public PageResult<ItemResponseDto> getSearchItems(String keyword, PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = itemRepository.findByName(keyword, pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        return PageResult.from(itemList);
    }
}
