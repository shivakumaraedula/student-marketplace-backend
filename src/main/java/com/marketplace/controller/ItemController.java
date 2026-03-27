package com.marketplace.controller;

import com.marketplace.dto.ItemDTO;
import com.marketplace.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<ItemDTO.Response>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(itemService.searchItems(search, page, size));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(itemService.getItemsByCategory(category, page, size));
        }
        return ResponseEntity.ok(itemService.getAllItems(page, size, sortBy, direction));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ItemDTO.Response>> getFeaturedItems() {
        return ResponseEntity.ok(itemService.getFeaturedItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO.Response> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<ItemDTO.Response> createItem(
            @Valid @RequestBody ItemDTO.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createItem(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO.Response> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemDTO.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(itemService.updateItem(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        itemService.deleteItem(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ItemDTO.Response>> getUserItems(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(itemService.getUserItems(userId, page, size));
    }
}
