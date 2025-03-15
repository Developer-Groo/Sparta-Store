package com.example.Sparta_Store.domain.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public <T> void putObject(String key, T object) {
        try {
            Map<String, Object> objectMap = objectMapper.convertValue(object, new TypeReference<>() {
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

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public <T> void pushToList(String key, T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            redisTemplate.opsForList().rightPush(key, json);
        } catch (Exception e) {
            throw new RuntimeException("pushToList 에러", e);
        }
    }

    public <T> List<T> getList(String key, Class<T> type) {
        try {
            List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
            if(list == null) return List.of();
            return list.stream()
                    .map(item -> {
                        try {
                            return objectMapper.readValue(item.toString(), type);
                        } catch (Exception e) {
                            throw new RuntimeException("getList 변환 에러", e);
                        }
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("getList 에러", e);
        }
    }

    public <T> void updateListElement(String key, int index, T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            redisTemplate.opsForList().set(key, index, json);
        } catch (Exception e) {
            throw new RuntimeException("updateListElement 에러", e);
        }
    }

    public <T> void removeFromList(String key, T object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            redisTemplate.opsForList().remove(key, 1, json);
        } catch (Exception e) {
            throw new RuntimeException("removeFromList 에러", e);
        }
    }
}
