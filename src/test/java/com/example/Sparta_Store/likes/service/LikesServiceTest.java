package com.example.Sparta_Store.likes.service;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.likes.dto.response.LikeResponseDto;
import com.example.Sparta_Store.likes.entity.Likes;
import com.example.Sparta_Store.likes.repository.LikesRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LikesServiceTest {

    @InjectMocks
    private LikesService likesService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private LikesRepository likesRepository;

    private User user;
    private Item item;
    private Likes likes;

    @BeforeEach
    void setUp() {
        user = new User("test@test.com", "password", "테스트1", null, null);
        item = new Item(1L, "상품2", "img.jpa", 1000, null, null, null, null);
        likes = new Likes(1L, user, item);
    }

    @Test
    @DisplayName("찜 생성 성공")
    void addLikes(){
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(likesRepository.findByUserAndItem(user,item))
                .thenReturn(Optional.empty());
        assertDoesNotThrow(() -> likesService.addLike(1L,1L));
        verify(likesRepository, times(1))
                .save(any(Likes.class));
    }

    @Test
    @DisplayName("찜 생성 실패 - 이미 찜한 상품")
    void addLikes_Fall_NotFound() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(likesRepository.findByUserAndItem(user,item))
                .thenReturn(Optional.of(likes));
        assertThrows(CustomException.class, () -> likesService.addLike(1L, 1L));
    }

    @Test
    @DisplayName("찜 목록 성공")
    void getLikeList() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(likesRepository.findAllByUser(user))
                .thenReturn(List.of(likes));
        List<LikeResponseDto> likesList = likesService.getLikeList(1L);
        assertEquals(1, likesList.size());
    }

    @Test
    @DisplayName("찜 취소 성공")
    void removeLike() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(likesRepository.findByUserAndItem(user,item))
                .thenReturn(Optional.of(likes));
        assertDoesNotThrow(() -> likesService.removeLike(1L, 1L));
        verify(likesRepository, times(1))
                .delete(likes);
    }

    @Test
    @DisplayName("찜 취소 실패 - 찜하지 않은 상품")
    void removeLike_Fall_NotFound(){
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        when(likesRepository.findByUserAndItem(user,item))
                .thenReturn(Optional.empty());
        assertThrows(CustomException.class, () -> likesService.removeLike(1L, 1L));
    }
}