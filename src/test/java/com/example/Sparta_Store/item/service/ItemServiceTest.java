package com.example.Sparta_Store.item.service;

import com.example.Sparta_Store.domain.item.service.ItemService;
import com.example.Sparta_Store.exception.global.CustomException;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.exception.item.ItemErrorCode;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.domain.orderItem.entity.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ItemServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    private Item item1, item2, item3;

    @BeforeEach
    void setUp() {
        item1 = itemRepository.save(Item.toEntity("상품1", "", 1000, "", 110, null));
        item2 = itemRepository.save(Item.toEntity("상품2", "", 1000, "", 10, null));
        item3 = itemRepository.save(Item.toEntity("상품3", "", 1000, "", 10, null));
    }

    @Test
    @Transactional
    @DisplayName("재고 감소 성공 - 트랜잭션 커밋")
    void shouldDecreaseStock_Success() {
        // given
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, null, item1, 1000, 3),
                new OrderItem(2L, null, item2, 1000, 2),
                new OrderItem(3L, null, item3, 1000, 1)
        );

        // when
        itemService.decreaseStock(orderItems);

        // then
        assertThat(itemRepository.findById(item1.getId()).get().getStockQuantity()).isEqualTo(107);
        assertThat(itemRepository.findById(item2.getId()).get().getStockQuantity()).isEqualTo(8);
        assertThat(itemRepository.findById(item3.getId()).get().getStockQuantity()).isEqualTo(9);
    }

    @Test
    @DisplayName("재고 감소 실패 - 트랙잭션 롤백")
    void shouldRollbackTransaction_When_StockIsInsufficient() {
        // given
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, null, item1, 1000, 3),
                new OrderItem(2L, null, item2, 1000, 2),
                new OrderItem(3L, null, item3, 1000, 20)
        );

        // when
        assertThatThrownBy(() -> itemService.decreaseStock(orderItems))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ItemErrorCode.OUT_OF_STOCK);

        // then
        assertThat(itemRepository.findById(item1.getId()).get().getStockQuantity()).isEqualTo(110);
        assertThat(itemRepository.findById(item2.getId()).get().getStockQuantity()).isEqualTo(10);
        assertThat(itemRepository.findById(item3.getId()).get().getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("여러개의 요청이 동시에 같은 상품의 재고 감소 요청 - 동시성 문제 X")
    void shouldHandleConcurrentStock_Decrease() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    log.info("[Thread-{}] 실행 시작", Thread.currentThread().getName());

                    itemService.decreaseStock(List.of(new OrderItem(1L, null, item1, 1000, 1)));

                    log.info("[Thread-{}] 데이터 수정 완료: 현재 stock = {}",
                            Thread.currentThread().getName(),
                            itemRepository.findById(item1.getId()).get().getStockQuantity());
                } finally {
                    latch.countDown();
                    log.info("[Thread-{}] 작업 완료, countDown 호출", Thread.currentThread().getName());
                }
            });
        }
        latch.await();
        log.info("모든 스레드 작업 완료");

        // then
        assertThat(itemRepository.findById(item1.getId()).get().getStockQuantity()).isEqualTo(110 - threadCount);
    }
}