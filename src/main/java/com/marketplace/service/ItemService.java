package com.marketplace.service;

import com.marketplace.dto.ItemDTO;
import com.marketplace.entity.Item;
import com.marketplace.entity.User;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ItemRepository;
import com.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public Page<ItemDTO.Response> getAllItems(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return itemRepository.findByStatus(Item.Status.ACTIVE, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO.Response> getItemsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return itemRepository.findByCategoryAndStatus(category, Item.Status.ACTIVE, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO.Response> searchItems(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return itemRepository.searchItems(query, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ItemDTO.Response> getFeaturedItems() {
        return itemRepository.findByFeaturedTrueAndStatus(Item.Status.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ItemDTO.Response getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        // Increment view count
        item.setViewCount(item.getViewCount() + 1);
        itemRepository.save(item);
        return toResponse(item);
    }

    @Transactional
    public ItemDTO.Response createItem(ItemDTO.CreateRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> savedImages = request.getImages().stream().map(img -> {
            try {
                if (img.startsWith("data:image")) {
                    return "/api/files/" + fileService.saveBase64Image(img);
                }
                return img;
            } catch (Exception e) {
                return null;
            }
        }).filter(img -> img != null).collect(Collectors.toList());

        Item item = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .buyPrice(request.getBuyPrice())
                .rentPriceDaily(request.getRentPriceDaily())
                .rentPriceWeekly(request.getRentPriceWeekly())
                .rentPriceMonthly(request.getRentPriceMonthly())
                .availableForRent(request.isAvailableForRent())
                .availableForSale(request.isAvailableForSale())
                .category(request.getCategory())
                .condition(request.getCondition() != null ? request.getCondition() : Item.Condition.GOOD)
                .images(savedImages)
                .tags(request.getTags())
                .location(request.getLocation())
                .seller(seller)
                .status(Item.Status.ACTIVE)
                .build();

        return toResponse(itemRepository.save(item));
    }

    @Transactional
    public ItemDTO.Response updateItem(Long id, ItemDTO.CreateRequest request, String sellerEmail) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        if (!item.getSeller().getEmail().equals(sellerEmail)) {
            throw new BadRequestException("You are not authorized to update this item");
        }

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setBuyPrice(request.getBuyPrice());
        item.setRentPriceDaily(request.getRentPriceDaily());
        item.setRentPriceWeekly(request.getRentPriceWeekly());
        item.setRentPriceMonthly(request.getRentPriceMonthly());
        item.setAvailableForRent(request.isAvailableForRent());
        item.setAvailableForSale(request.isAvailableForSale());
        item.setCategory(request.getCategory());
        if (request.getCondition() != null) item.setCondition(request.getCondition());
        if (request.getImages() != null) item.setImages(request.getImages());
        if (request.getTags() != null) item.setTags(request.getTags());
        if (request.getLocation() != null) item.setLocation(request.getLocation());

        return toResponse(itemRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long id, String sellerEmail) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        if (!item.getSeller().getEmail().equals(sellerEmail)) {
            throw new BadRequestException("You are not authorized to delete this item");
        }

        item.setStatus(Item.Status.INACTIVE);
        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO.Response> getUserItems(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return itemRepository.findBySeller_IdAndStatus(userId, Item.Status.ACTIVE, pageable)
                .map(this::toResponse);
    }

    private ItemDTO.Response toResponse(Item item) {
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
