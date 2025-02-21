package com.example.Sparta_Store.item.service;

import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    public PageResult<ItemResponseDto> getItems(boolean inStock, PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = itemRepository.findAllByStockCondition(inStock, pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        return PageResult.from(itemList);
    }

    public PageResult<ItemResponseDto> getSearchItems(boolean inStock, String keyword, PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = itemRepository.findByNameAndStockCondition(inStock, keyword, pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        return PageResult.from(itemList);
    }

    @Transactional
    public void decreaseStock(List<OrderItem> orderItemList) {
        List<Long> idList = orderItemList
                .stream().map(orderItem -> orderItem.getItem().getId())
                .toList();

        itemRepository.findAllByIdWithLock(idList);

        orderItemList
                .forEach(orderItem -> {
                    int updateRows = itemRepository.decreaseStock(orderItem.getItem().getId(), orderItem.getQuantity());
                    if (updateRows == 0) {
                        throw new IllegalArgumentException(
                                "재고가 부족합니다. (상품Id: " + orderItem.getItem().getId() +
                                        ", " +
                                        "상품이름: " + orderItem.getItem().getName() + ")"
                        );
                    }
                });
    }
}
