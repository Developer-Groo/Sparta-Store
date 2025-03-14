package com.example.Sparta_Store.domain.likes.repository;

import com.example.Sparta_Store.domain.item.entity.Item;
import com.example.Sparta_Store.domain.likes.entity.Likes;
import com.example.Sparta_Store.domain.user.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByUserAndItem(Users user, Item item);

    List<Likes> findAllByUser(Users user);

    @Query("SELECT COUNT(l) FROM Likes l WHERE l.item.id = :itemId ")
    Long countByItemId(@Param("itemId") Long itemId);

    @Query("SELECT DISTINCT l.item.id FROM Likes l")
    List<Long> findDistinctItemIds();

    @EntityGraph(attributePaths = {"user"})
    List<Likes> findUserByItemId(Long itemId);
}
