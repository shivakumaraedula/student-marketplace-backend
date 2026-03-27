package com.marketplace.repository;

import com.marketplace.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUser_Id(Long userId);
    Optional<Wishlist> findByUser_IdAndItem_Id(Long userId, Long itemId);
    boolean existsByUser_IdAndItem_Id(Long userId, Long itemId);
    void deleteByUser_IdAndItem_Id(Long userId, Long itemId);
}
