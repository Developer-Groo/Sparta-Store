package com.example.Sparta_Store.likes.service;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.likes.dto.response.LikeResponseDto;
import com.example.Sparta_Store.likes.entity.Likes;
import com.example.Sparta_Store.likes.exception.LikesErrorCode;
import com.example.Sparta_Store.likes.repository.LikesRepository;
import com.example.Sparta_Store.user.entity.Users;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.*;


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

    private Users user;
    private Item item;
    private Likes likes;

    @BeforeEach
    void setUp() {
        user = new Users("test@test.com", "password", "테스트1", null, null);
        item = new Item(1L, "상품2", "img.jpa", 1000, null, null, null, null);
        likes = new Likes(1L, user, item,1,1);
    }

    @Test
    @DisplayName("찜 생성 성공")
    void addLikes(){
        // given
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        given(itemRepository.findByIdWithLock(1L))
                .willReturn(Optional.of(item));
        given(likesRepository.findByUserAndItem(user,item))
                .willReturn(Optional.empty());
        // when & then
        assertDoesNotThrow(() -> likesService.addLike(1L,1L));
        then(likesRepository).should(times(1)).save(any(Likes.class));
    }

    @Test
    @DisplayName("찜 생성 실패 - 이미 찜한 상품")
    void addLikes_Fall_NotFound() {
        // given
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        given(itemRepository.findByIdWithLock(1L))
                .willReturn(Optional.of(item));
        given(likesRepository.findByUserAndItem(user,item))
                .willReturn(Optional.of(likes));
        // when & then
        assertThatThrownBy(() -> likesService.addLike(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(LikesErrorCode.PRODUCT_ALREADY_WISHLIST);
    }

    @Test
    @DisplayName("찜 목록 성공")
    void getLikeList() {
        // given
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        given(likesRepository.findAllByUser(user))
                .willReturn(List.of(likes));
        // when
        List<LikeResponseDto> likesList = likesService.getLikeList(1L);
        // then
        assertThat(likesList).hasSize(1);
    }

    @Test
    @DisplayName("찜 취소 성공")
    void removeLike() {
        // given
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        given(itemRepository.findById(1L))
                .willReturn(Optional.of(item));
        given(likesRepository.findByUserAndItem(user,item))
                .willReturn(Optional.of(likes));
        // when & then
        assertDoesNotThrow(() -> likesService.removeLike(1L, 1L));
        then(likesRepository).should(times(1)).delete(any(Likes.class));
    }

    @Test
    @DisplayName("찜 취소 실패 - 찜하지 않은 상품")
    void removeLike_Fall_NotFound(){
        // given
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        given(itemRepository.findById(1L))
                .willReturn(Optional.of(item));
        given(likesRepository.findByUserAndItem(user,item))
                .willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> likesService.removeLike(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(LikesErrorCode.PRODUCT_NOT_WISHLIST);
    }
}