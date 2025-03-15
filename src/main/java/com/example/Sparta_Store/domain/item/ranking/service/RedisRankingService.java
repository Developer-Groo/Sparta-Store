package com.example.Sparta_Store.domain.item.ranking.service;

import com.example.Sparta_Store.domain.item.ranking.repository.RedisRankingRepository;
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
        String category = redisRankingRepository.findCategoryNameByItemId(itemId);
        String itemName = redisRankingRepository.findItemNameByItemId(itemId);
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Double currentScore = zSetOps.score(category, itemName);

        if (currentScore == null) {
            zSetOps.add(category, itemName, 1);
        } else {
            zSetOps.incrementScore(category, itemName, 1);
        }
    }

    @Scheduled(cron = "0 0 0 * * Mon")
    public void clearRedisCache() {
        Set<String> categories = redisTemplate.keys("*");

        for (String category : categories) {
            redisTemplate.delete(category);
        }
        synchronizedSet.clear();
    }
}