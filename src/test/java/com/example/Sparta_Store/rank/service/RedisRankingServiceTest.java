package com.example.Sparta_Store.rank.service;

import com.example.Sparta_Store.category.entity.Category;
import com.example.Sparta_Store.ranking.RedisRankingRepository;
import com.example.Sparta_Store.ranking.RedisRankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class RedisRankingServiceTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private RedisRankingRepository redisRankingRepository;

    @InjectMocks
    private RedisRankingService redisRankingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddToRedis_NewItem() {
        //given
        Long itemId = 1L;
        String categoryName = "Category1";
        String itemName = "Item1";

        when(redisRankingRepository.findCategoryNameByItemId(itemId)).thenReturn(categoryName);
        when(redisRankingRepository.findItemNameByItemId(itemId)).thenReturn(itemName);

        ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(zSetOps.score(categoryName, itemName)).thenReturn(null);

        //when
        redisRankingService.addToRedis(itemId);

        //then
        verify(zSetOps).add(categoryName, itemName, 1);
    }

    @Test
    public void testClearRedisCache() {
        // Given
        Set<String> categories = new HashSet<>();
        categories.add("category1");
        categories.add("category2");

        when(redisTemplate.keys("*")).thenReturn(categories);

        // When
        redisRankingService.clearRedisCache();

        // Then
        for (String category : categories) {
            verify(redisTemplate, times(1)).delete(category);
        }
    }
}