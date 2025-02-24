package com.example.Sparta_Store.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private HashOperations<String, String, Object> hashOperations;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public <T> void putObject(String key, T object) {
        try {
            Map<String, Object> objectMap = objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
            });
            hashOperations.putAll(key, objectMap);
        } catch (Exception e) {
            throw new RuntimeException("Object Mapper 에러", e);
        }

    }

    public <T> T getObject(String key, Class<T> type) {
        try {
            Map<String, Object> objectMap = hashOperations.entries(key);
            return objectMapper.convertValue(objectMap, type);
        } catch (Exception e) {
            throw new RuntimeException("getObject 에러", e);
        }
    }

    public <T> List<T> getObjectList (String key, Class<T> type) {
        try {
            Map<String, Object> objectMap = hashOperations.entries(key);
            return objectMap.values().stream().map(
                    value -> objectMapper.convertValue(value, type)
            ).toList();
        } catch (Exception e) {
            throw new RuntimeException("getObjectList 에러", e);
        }
    }

    public void delete (String key) {
        redisTemplate.delete(key);
    }



}
