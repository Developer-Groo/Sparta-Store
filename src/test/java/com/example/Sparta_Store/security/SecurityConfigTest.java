package com.example.Sparta_Store.security;

import com.example.Sparta_Store.address.entity.Address;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.service.ItemService;
import com.example.Sparta_Store.oAuth.jwt.UserRoleEnum;
import com.example.Sparta_Store.user.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        Address address = new Address("경기도", "테스트길", "12345");
        Users user = new Users(1L, UUID.randomUUID().toString(), "테스트유저", "email@test.com", "Pw1234!!!", address, false, null, null, UserRoleEnum.USER);
        Item item = new Item(1L, "상품1", "img1@test.com", 10000, "상품1입니다.", 100, null, null);
    }

    @Test
    @DisplayName("보안 설정 테스트: 특정 URL에 대한 접근 제어 확인")
    public void testAccessControl() throws Exception {
        // 회원가입, 로그인, CSS 파일 요청은 허용되어야 함
        mockMvc.perform(get("/users/signUp")).andExpect(status().isOk());
        mockMvc.perform(get("/users/login")).andExpect(status().isOk());

        // 아이템 조회는 허용되어야 함
        mockMvc.perform(get("/items")).andExpect(status().isOk());

        // ADMIN 권한이 필요한 URL 은 접근이 거부되어야 함
        mockMvc.perform(get("/admin/dashboard")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CORS 설정 테스트")
    public void testCorsConfiguration() throws Exception {
        mockMvc.perform(get("/items")
                        .header("Origin", "http://example.com"))
                .andExpect(status().isOk());
    }
}
