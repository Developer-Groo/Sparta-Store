package com.example.Sparta_Store.item.service;

import com.example.Sparta_Store.cartItem.entity.CartItem;
import com.example.Sparta_Store.item.dto.response.ItemResponseDto;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemQueryRepository;
import com.example.Sparta_Store.item.repository.ItemRepository;
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
    private final ItemQueryRepository queryRepository;

    public PageResult<ItemResponseDto> getItems(PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = itemRepository.findAll(pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        return PageResult.from(itemList);
    }

    public PageResult<ItemResponseDto> getSearchItems(String keyword, PageQuery pageQuery) {
        Page<ItemResponseDto> itemList = queryRepository.findByName(keyword, pageQuery.toPageable())
                .map(ItemResponseDto::toDto);

        return PageResult.from(itemList);
    }

    @Transactional
    public void decreaseStock(List<CartItem> cartItemList) { //todo 추후 동시성 문제, 성능개선
        for (CartItem cartItem : cartItemList) {
            int quantity = cartItem.getQuantity();
            Item item = cartItem.getItem();
            item.decreaseStock(quantity);
        }
    }
}
