package com.example.Sparta_Store.item.service;

import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.orderItem.entity.OrderItem;
import com.example.Sparta_Store.util.PageQuery;
import com.example.Sparta_Store.util.PageResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void decreaseStock(List<OrderItem> orderItemList) { //todo 추후 동시성 문제, 성능개선
        for (OrderItem orderItem : orderItemList) {
            int quantity = orderItem.getQuantity();
            Item item = orderItem.getItem();
            item.decreaseStock(quantity);
        }
    }
}
