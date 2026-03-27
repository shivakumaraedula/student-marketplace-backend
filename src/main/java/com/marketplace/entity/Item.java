package com.marketplace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Positive
    @Column(name = "buy_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal buyPrice;

    @Positive
    @Column(name = "rent_price_daily", precision = 10, scale = 2)
    private BigDecimal rentPriceDaily;

    @Positive
    @Column(name = "rent_price_weekly", precision = 10, scale = 2)
    private BigDecimal rentPriceWeekly;

    @Positive
    @Column(name = "rent_price_monthly", precision = 10, scale = 2)
    private BigDecimal rentPriceMonthly;

    @Column(name = "is_available_for_rent")
    @Builder.Default
    private boolean availableForRent = false;

    @Column(name = "is_available_for_sale")
    @Builder.Default
    private boolean availableForSale = true;

    @NotBlank
    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    @Builder.Default
    private Condition condition = Condition.GOOD;

    @ElementCollection
    @CollectionTable(name = "item_images", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> images;

    @ElementCollection
    @CollectionTable(name = "item_tags", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "tag")
    private List<String> tags;

    private String location;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "wishlist_count")
    @Builder.Default
    private Integer wishlistCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean featured = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Condition {
        LIKE_NEW, EXCELLENT, GOOD, FAIR, POOR
    }

    public enum Status {
        ACTIVE, SOLD, INACTIVE
    }
}
