package com.marketplace.repository;

import com.marketplace.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByStatus(Item.Status status, Pageable pageable);

    Page<Item> findByCategoryAndStatus(String category, Item.Status status, Pageable pageable);

    Page<Item> findBySeller_IdAndStatus(Long sellerId, Item.Status status, Pageable pageable);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.seller WHERE i.featured = true AND i.status = :status")
    List<Item> findByFeaturedTrueAndStatus(@Param("status") Item.Status status);

    @Query("SELECT i FROM Item i WHERE i.status = 'ACTIVE' AND (" +
           "LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Item> searchItems(@Param("query") String query, Pageable pageable);

    @Query("SELECT i.category, COUNT(i) FROM Item i WHERE i.status = 'ACTIVE' GROUP BY i.category")
    List<Object[]> countByCategory();

    @Query("SELECT COUNT(i) FROM Item i WHERE i.status = 'ACTIVE'")
    Long countActiveListings();
}
