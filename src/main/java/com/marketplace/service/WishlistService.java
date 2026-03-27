package com.marketplace.service;

import com.marketplace.dto.ItemDTO;
import com.marketplace.entity.Item;
import com.marketplace.entity.User;
import com.marketplace.entity.Wishlist;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ItemRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<ItemDTO.Response> getWishlist(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return wishlistRepository.findByUser_Id(user.getId())
                .stream()
                .map(w -> toItemResponse(w.getItem()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToWishlist(Long itemId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if (wishlistRepository.existsByUser_IdAndItem_Id(user.getId(), itemId)) {
            throw new BadRequestException("Item already in wishlist");
        }

        Wishlist wishlist = Wishlist.builder().user(user).item(item).build();
        wishlistRepository.save(wishlist);

        item.setWishlistCount(item.getWishlistCount() + 1);
        itemRepository.save(item);
    }

    @Transactional
    public void removeFromWishlist(Long itemId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!wishlistRepository.existsByUser_IdAndItem_Id(user.getId(), itemId)) {
            throw new BadRequestException("Item not in wishlist");
        }

        wishlistRepository.deleteByUser_IdAndItem_Id(user.getId(), itemId);

        Item item = itemRepository.findById(itemId).orElse(null);
        if (item != null && item.getWishlistCount() > 0) {
            item.setWishlistCount(item.getWishlistCount() - 1);
            itemRepository.save(item);
        }
    }

    private ItemDTO.Response toItemResponse(Item item) {
        User seller = item.getSeller();
        return ItemDTO.Response.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .buyPrice(item.getBuyPrice())
                .rentPriceDaily(item.getRentPriceDaily())
                .rentPriceWeekly(item.getRentPriceWeekly())
                .rentPriceMonthly(item.getRentPriceMonthly())
                .availableForRent(item.isAvailableForRent())
                .availableForSale(item.isAvailableForSale())
                .category(item.getCategory())
                .condition(item.getCondition())
                .images(item.getImages())
                .tags(item.getTags())
                .location(item.getLocation())
                .viewCount(item.getViewCount())
                .wishlistCount(item.getWishlistCount())
                .featured(item.isFeatured())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt())
                .seller(ItemDTO.SellerInfo.builder()
                        .id(seller.getId())
                        .name(seller.getName())
                        .university(seller.getUniversity())
                        .profileImage(seller.getProfileImage())
                        .rating(seller.getRating())
                        .totalSales(seller.getTotalSales())
                        .build())
                .build();
    }
}
