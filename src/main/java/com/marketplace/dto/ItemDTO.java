package com.marketplace.dto;

import com.marketplace.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ItemDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Title is required")
        private String title;

        private String description;

        @NotNull(message = "Buy price is required")
        @Positive(message = "Buy price must be positive")
        private BigDecimal buyPrice;

        @Positive(message = "Rent price daily must be positive")
        private BigDecimal rentPriceDaily;

        @Positive(message = "Rent price weekly must be positive")
        private BigDecimal rentPriceWeekly;

        @Positive(message = "Rent price monthly must be positive")
        private BigDecimal rentPriceMonthly;

        private boolean availableForRent;

        private boolean availableForSale;

        @NotBlank(message = "Category is required")
        private String category;

        private Item.Condition condition;

        private List<String> images;

        private List<String> tags;

        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private BigDecimal buyPrice;
        private BigDecimal rentPriceDaily;
        private BigDecimal rentPriceWeekly;
        private BigDecimal rentPriceMonthly;
        private boolean availableForRent;
        private boolean availableForSale;
        private String category;
        private Item.Condition condition;
        private List<String> images;
        private List<String> tags;
        private String location;
        private Integer viewCount;
        private Integer wishlistCount;
        private boolean featured;
        private Item.Status status;
        private SellerInfo seller;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SellerInfo {
        private Long id;
        private String name;
        private String university;
        private String profileImage;
        private Double rating;
        private Integer totalSales;
    }
}
