package com.example.Sparta_Store.domain.likes.service;

import com.example.Sparta_Store.exception.CustomException;
import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.item.repository.ItemRepository;
import com.example.Sparta_Store.domain.likes.dto.response.LikeResponseDto;
import com.example.Sparta_Store.domain.likes.entity.Likes;
import com.example.Sparta_Store.domain.likes.exception.LikesErrorCode;
import com.example.Sparta_Store.domain.likes.repository.LikesRepository;
import com.example.Sparta_Store.domain.user.entity.Users;
import com.example.Sparta_Store.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void addLike(Long itemId, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(LikesErrorCode.NOT_EXISTS_USER));
        Item item = itemRepository.findByIdWithLock(itemId)
                .orElseThrow(() -> new CustomException(LikesErrorCode.NOT_EXISTS_PRODUCT));

        if (likesRepository.findByUserAndItem(user, item).isPresent()) {
            throw new CustomException(LikesErrorCode.PRODUCT_ALREADY_WISHLIST);
        }
        likesRepository.save(new Likes(user, item));
    }

    // 찜 목록
    public List<LikeResponseDto> getLikeList(Long userId) {

        Users user = userRepository.findById(userId).
                orElseThrow(() -> new CustomException(LikesErrorCode.NOT_EXISTS_USER));

        List<Likes> likeList = likesRepository.findAllByUser(user);

        return likeList.stream()
                .map(LikeResponseDto::toDto)
                .toList();
    }

    // 총 찜 갯수
    public Long getLikeCount(Long itemId) {
        return likesRepository.countByItemId(itemId);
    }

    public List<String> getUserEmailsByItemId(Long itemId) {
        List<Likes> likesList = likesRepository.findUserByItemId(itemId);
        return likesList.stream()
                .map(likes -> likes.getUser().getEmail())
                .toList();
    }

    // 찜 취소
    @Transactional
    public void removeLike(Long itemId, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(LikesErrorCode.NOT_EXISTS_USER));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(LikesErrorCode.NOT_EXISTS_PRODUCT));
        Likes likes = likesRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new CustomException(LikesErrorCode.PRODUCT_NOT_WISHLIST));

        likesRepository.delete(likes);
    }

}
