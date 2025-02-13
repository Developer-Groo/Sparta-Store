package com.example.Sparta_Store.likes.service;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.item.repository.ItemRepository;
import com.example.Sparta_Store.likes.entity.Likes;
import com.example.Sparta_Store.likes.repository.LikesRepository;
import com.example.Sparta_Store.user.entity.User;
import com.example.Sparta_Store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public void addLike(Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        Item item = itemRepository.findById(itemId).orElseThrow(()-> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        if(likesRepository.findByUserAndItem(user, item).isPresent()) {
            throw new IllegalArgumentException("이미 찜한 상품 입니다.");
        }

        likesRepository.save(new Likes(user, item));
    }

    // 찜 목록
    public List<Likes> getLikeList(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        return likesRepository.findAllByUser(user);
    }

    // 찜 취소
    public void removeLike(Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        Item item = itemRepository.findById(itemId).orElseThrow(()-> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        Likes likes = likesRepository.findByUserAndItem(user, item).orElseThrow(()-> new IllegalArgumentException("해당 상품을 찜하지 않았습니다."));

        likesRepository.delete(likes);
    }
}
