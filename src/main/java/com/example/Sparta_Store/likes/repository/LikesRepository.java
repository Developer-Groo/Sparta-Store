package com.example.Sparta_Store.likes.repository;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.likes.entity.Likes;
import com.example.Sparta_Store.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserAndItem(User user, Item item);

    List<Likes> findAllByUser(User user);
}
