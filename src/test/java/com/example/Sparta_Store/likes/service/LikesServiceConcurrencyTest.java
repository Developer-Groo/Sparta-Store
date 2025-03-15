package com.example.Sparta_Store.likes.service;

import com.example.Sparta_Store.domain.address.entity.Address;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.domain.likes.repository.LikesRepository;
import com.example.Sparta_Store.domain.likes.service.LikesService;
import com.example.Sparta_Store.domain.users.entity.Users;
import com.example.Sparta_Store.domain.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class LikesServiceConcurrencyTest {

    @Autowired
    private LikesService likesService;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;
    private Users user;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 데이터 초기화
        likesRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        item = Item.toEntity("상품1", "img.jpa", 1000, "null", 10, null);
        itemRepository.save(item);

        Address address = new Address("서울광역시", "봉천로", "2343");
        user = new Users("test@naver.com", "password", "테스트1", address, null);
        user = userRepository.save(user);
    }

    @Test
    @Rollback
    @DisplayName("동시 추가 좋아요 - 비관적 락")
    void ConcurrentAddLike() throws InterruptedException {
        // given
        int number = 50;
        List<Users> usersList = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Address address = new Address("서울광역시", "봉천로", "2343");
            Users user = new Users("test4_" + i + "@naver.com", "password", "테스트1", address, null);
            user = userRepository.save(user);
            usersList.add(user);
        }
        // when
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(number);

        for (Users user : usersList) {
            executor.submit(() -> {
                try {
                    likesService.addLike(item.getId(), user.getId());
                } catch (Exception e) {
                    log.info("addLike 예외: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then
        Long likeCount = likesRepository.countByItemId(item.getId());
        log.info("상품의 최종 찜(좋아요) 개수 likeCount {} ", likeCount);

        assertEquals(number, likeCount.intValue(), "모든 사용자 요청이 정상 처리되어야 합니다.");
    }

    @Test
    @Rollback
    @DisplayName("동일 사용자가 동시에 좋아요 요청 시, 한 요청만 성공하고 나머지는 실패한다.")
    void TestAddLikeSameUserFailure() throws InterruptedException, ExecutionException {
        // given
        int number = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(number);
        List<Future<Object>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < number; i++) {
            futures.add(executor.submit(() -> {
                try {
                    likesService.addLike(item.getId(), user.getId());
                    return "success";
                } catch (Exception e) {
                    return e;
                } finally {
                    latch.countDown();
                }
            }));
        }
        latch.await();
        executor.shutdown();

        // then
        int successCount = 0;
        int failureCount = 0;
        for (Future<Object> future : futures) {
            Object result = future.get();
            if (result instanceof String && "success".equals(result)) {
                successCount++;
            } else if (result instanceof Exception) {
                failureCount++;
            }
        }
        assertEquals(1, successCount, "동일 사용자가 중복 좋아요 시 한 요청만 성공해야 한다.");
        assertEquals(number - 1, failureCount, " 동일 사용자가 중복 좋아요 시 나머지 요청은 실패해야 한다.");

        Long likeCount = likesRepository.countByItemId(item.getId());
        assertEquals(1, likeCount.intValue(), "좋아요 기록은 단 하나여야 한다.");
    }
}
