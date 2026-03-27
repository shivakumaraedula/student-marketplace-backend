package com.marketplace.service;

import com.marketplace.entity.*;
import com.marketplace.exception.*;
import com.marketplace.repository.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository   userRepository;
    private final ItemRepository   itemRepository;

    public Page<Map<String, Object>> getSellerReviews(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findBySeller_Id(sellerId, pageable)
                .map(r -> Map.of(
                        "id",       r.getId(),
                        "rating",   r.getRating(),
                        "comment",  r.getComment() != null ? r.getComment() : "",
                        "reviewer", Map.of("id", r.getReviewer().getId(), "name", r.getReviewer().getName()),
                        "itemId",   r.getItem() != null ? r.getItem().getId() : null,
                        "createdAt",r.getCreatedAt()
                ));
    }

    @Transactional
    public void createReview(Long sellerId, Long itemId, int rating, String comment, String reviewerEmail) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        if (reviewRepository.existsByReviewer_IdAndItem_Id(reviewer.getId(), itemId)) {
            throw new BadRequestException("You have already reviewed this item");
        }

        Item item = itemId != null ? itemRepository.findById(itemId).orElse(null) : null;

        Review review = Review.builder()
                .rating(rating)
                .comment(comment)
                .reviewer(reviewer)
                .seller(seller)
                .item(item)
                .build();
        reviewRepository.save(review);

        // Recalculate seller average rating
        Double avg = reviewRepository.findAverageRating(sellerId);
        if (avg != null) {
            seller.setRating(Math.round(avg * 10.0) / 10.0);
            userRepository.save(seller);
        }
    }
}
