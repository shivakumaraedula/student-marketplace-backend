package com.marketplace.controller;

import com.marketplace.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<Map<String, Object>>> getSellerReviews(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getSellerReviews(sellerId, page, size));
    }

    @PostMapping
    public ResponseEntity<Void> createReview(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long   sellerId = Long.valueOf(body.get("sellerId").toString());
        Long   itemId   = body.get("itemId") != null ? Long.valueOf(body.get("itemId").toString()) : null;
        int    rating   = Integer.parseInt(body.get("rating").toString());
        String comment  = body.getOrDefault("comment", "").toString();
        reviewService.createReview(sellerId, itemId, rating, comment, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
