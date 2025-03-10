package com.example.Sparta_Store.ranking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RedisRankingService {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisRankingRepository redisRankingRepository;
    private final SortedSet<String> synchronizedSet = Collections.synchronizedSortedSet(new TreeSet<>());

    public void addToRedis(Long itemId) {
        // 아이템 ID를 통해 카테고리 값을 가져옴
        String category = redisRankingRepository.findCategoryNameByItemId(itemId);

        String itemName = redisRankingRepository.findItemNameByItemId(itemId);

        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        // Redis에 ZADD 명령어로 추가
        Double currentScore = zSetOps.score(category, itemName);

        if (currentScore == null) {
            // 아이템이 없으면 아이템을 생성한다.
            zSetOps.add(category, itemName, 1);
        } else {
            // 아이템이 있으면 점수를 증가시킨다
            zSetOps.incrementScore(category, itemName, 1);
        }
    }

    @Scheduled(cron = "0 0 0 * * Mon")
    public void clearRedisCache() {
        // 한주에 한번 모든 카테고리의 데이터를 초기화하는 로직

        Set<String> categories = redisTemplate.keys("*"); // 모든 카테고리 키 가져오기
        if (categories != null) {
            for (String category : categories) {
                redisTemplate.delete(category); // 카테고리 삭제
            }
        }
        synchronizedSet.clear();
    }
}