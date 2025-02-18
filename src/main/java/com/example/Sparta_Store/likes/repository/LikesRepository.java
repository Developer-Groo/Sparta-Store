package com.example.Sparta_Store.likes.repository;

import com.example.Sparta_Store.item.entity.Item;
import com.example.Sparta_Store.likes.entity.Likes;
import com.example.Sparta_Store.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserAndItem(User user, Item item);

    List<Likes> findAllByUser(User user);

    @Query("SELECT COUNT(l) FROM Likes l WHERE l.item.id = :itemId ")
    Long countByItemId(@Param("itemId") Long itemId);

}
