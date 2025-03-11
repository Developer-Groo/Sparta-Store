package com.example.Sparta_Store.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ZSetOperations zSetOperations;
    private HashOperations<String, String, Object> hashOperations;
    private final ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate, @Qualifier("zSetOperations") ZSetOperations zSetOperations) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.zSetOperations = zSetOperations;
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

    public <T> List<T> getObjectList(String key, Class<T> type) {
        try {
            Map<String, Object> objectMap = hashOperations.entries(key);
            return objectMap.values().stream().map(
                    value -> objectMapper.convertValue(value, type)
            ).toList();
        } catch (Exception e) {
            throw new RuntimeException("getObjectList 에러", e);
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
                    .collect(Collectors.toList());
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
            // LREM key count value : count가 0이면 전체 제거
            // 여기서는 1개만 제거하도록 count 1 사용
            String json = objectMapper.writeValueAsString(object);
            redisTemplate.opsForList().remove(key, 1, json);
        } catch (Exception e) {
            throw new RuntimeException("removeFromList 에러", e);
        }
    }
}
