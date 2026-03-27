package com.marketplace.service;

import com.marketplace.entity.Item;
import com.marketplace.entity.Rental;
import com.marketplace.entity.User;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ItemRepository;
import com.marketplace.repository.RentalRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Rental rentItem(Long itemId, String renterEmail, int days) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if (item.getStatus() != Item.Status.ACTIVE || !item.isAvailableForRent()) {
            throw new BadRequestException("Item is not available for rent");
        }

        User renter = userRepository.findByEmail(renterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        BigDecimal rate = item.getRentPriceDaily();
        if (days >= 30 && item.getRentPriceMonthly() != null) {
            rate = item.getRentPriceMonthly().divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        } else if (days >= 7 && item.getRentPriceWeekly() != null) {
            rate = item.getRentPriceWeekly().divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP);
        }

        BigDecimal totalAmount = rate.multiply(BigDecimal.valueOf(days));

         Rental rental = Rental.builder()
                .item(item)
                .renter(renter)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(days))
                .totalAmount(totalAmount)
                .status(Rental.RentalStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Optionally, we could set item status to RENTED if it's not multi-availability
        // For simplicity, let's just create the rental record
        
        return rentalRepository.save(rental);
    }
}
