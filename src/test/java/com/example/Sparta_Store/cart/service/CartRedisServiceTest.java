package com.example.Sparta_Store.cart.service;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = CartRedisService.class)
@AutoConfigureMockMvc
class CartRedisServiceTest {

    @Autowired
    private CartRedisService cartRedisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        Address address = new Address("서울시", "테헤란로", "12345");
        user = new User("email1@email.com", "testUser", "김르탄", address);
//        item = new Item("갤럭시", "https://example.com/iphone15.jpg", "1,000,000", "")

    }



}