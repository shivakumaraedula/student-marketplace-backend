package com.marketplace.service;

import com.marketplace.entity.Item;
import com.marketplace.entity.Purchase;
import com.marketplace.entity.User;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ItemRepository;
import com.marketplace.repository.PurchaseRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Purchase buyItem(Long itemId, String buyerEmail) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if (item.getStatus() != Item.Status.ACTIVE || !item.isAvailableForSale()) {
            throw new BadRequestException("Item is not available for sale");
        }

        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (item.getSeller().getId().equals(buyer.getId())) {
            throw new BadRequestException("You cannot buy your own item");
        }

        Purchase purchase = Purchase.builder()
                .item(item)
                .buyer(buyer)
                .amount(item.getBuyPrice())
                .status(Purchase.PurchaseStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();

        item.setStatus(Item.Status.SOLD);
        itemRepository.save(item);

        return purchaseRepository.save(purchase);
    }
}
