package com.marketplace.controller;

import com.marketplace.entity.Purchase;
import com.marketplace.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buy")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/{itemId}")
    public ResponseEntity<Purchase> buyItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(purchaseService.buyItem(itemId, userDetails.getUsername()));
    }
}
