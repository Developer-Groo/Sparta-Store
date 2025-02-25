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
                .orElseThrow(()-> new CustomException(LikesErrorCode.NOT_EXISTS_USER));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(()-> new CustomException(LikesErrorCode.NOT_EXISTS_PRODUCT));

        if(likesRepository.findByUserAndItem(user, item).isPresent()) {
            throw new CustomException(LikesErrorCode.PRODUCT_ALREADY_WISHLIST);
        }

        likesRepository.save(new Likes(user, item));
    }

    // 찜 목록
    public List<LikeResponseDto> getLikeList(Long userId) {

        Users user = userRepository.findById(userId).
                orElseThrow(()-> new CustomException(LikesErrorCode.NOT_EXISTS_USER));

        List<Likes> likeList = likesRepository.findAllByUser(user);

        return likeList.stream()
                .map(LikeResponseDto::toDto)
                .toList();
    }

    public Long getLikeCount(Long itemId) {
        return likesRepository.countByItemId(itemId);
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
