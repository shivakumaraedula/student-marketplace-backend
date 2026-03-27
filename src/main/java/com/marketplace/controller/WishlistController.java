package com.marketplace.controller;

import com.marketplace.dto.ItemDTO;
import com.marketplace.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<ItemDTO.Response>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(wishlistService.getWishlist(userDetails.getUsername()));
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<Void> addToWishlist(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.addToWishlist(itemId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.removeFromWishlist(itemId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
