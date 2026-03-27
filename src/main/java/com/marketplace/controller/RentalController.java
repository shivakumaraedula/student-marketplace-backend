package com.marketplace.controller;

import com.marketplace.entity.Rental;
import com.marketplace.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rent")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/{itemId}")
    public ResponseEntity<Rental> rentItem(
            @PathVariable Long itemId, 
            @RequestParam int days, 
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rentalService.rentItem(itemId, userDetails.getUsername(), days));
    }
}
