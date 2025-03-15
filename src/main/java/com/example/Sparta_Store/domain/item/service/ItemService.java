package com.example.Sparta_Store.domain.item.service;

import com.example.Sparta_Store.domain.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.domain.item.dto.response.SelectItemResponseDto;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.domain.orderItem.entity.OrderItem;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    @Cacheable(value = "itemSearch", key = "#inStock", unless = "#result.content.isEmpty()")
    public PageResult<ItemResponseDto> getItems(boolean inStock, PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = itemRepository.findAllByStockCondition(inStock, pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        return PageResult.from(itemList);
    }

    @Cacheable(value = "itemSearch", key = "#inStock + '_' + #keyword", unless = "#result.content.isEmpty()")
    public PageResult<ItemResponseDto> getSearchItems(boolean inStock, String keyword, PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = itemRepository.findByNameAndStockCondition(inStock, keyword, pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        log.info("캐시 키 생성 - inStock: {}, keyword: {}, page: {}, size: {}", inStock, keyword, pageQuery.getPage(), pageQuery.getSize());

        return PageResult.from(itemList);
    }

    @Transactional
    public void decreaseStock(List<OrderItem> orderItemList) {
        List<Long> idList = orderItemList
                .stream()
                .map(orderItem -> orderItem.getItem().getId())
                .toList();

        log.info("[Thread-{}] Lock 획득 대기 중", Thread.currentThread().getName());
        Map<Long, Item> lockWithItems = itemRepository.findAllByIdWithLock(idList)
                .stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        orderItemList.forEach(orderItem -> {
            Item item = lockWithItems.get(orderItem.getItem().getId());
            item.decreaseStock(orderItem.getQuantity());
        });
    }

    public SelectItemResponseDto selectItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(()->new RuntimeException("해당아이템이 없습니다."));

        return SelectItemResponseDto.from(item);
    }
}
