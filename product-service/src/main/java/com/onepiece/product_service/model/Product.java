package com.onepiece.product_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "seller_id")
    private Integer sellerId;

    @Column(name = "product_model")
    private String productModel;

    @Column(name = "model_year")
    private Integer modelYear;

    @Column(name = "start_price")
    private Integer startPrice;

    @Column(name = "price_jump")
    private Integer priceJump;

    @Column(name = "detail")
    private String description;

    @Column(name = "auction_date")
    private LocalDate auctionDate;

    @Column(name = "auction_start_time")
    private LocalTime auctionStartTime;

    @Column(name = "auction_duration")
    private LocalTime auctionDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    public enum Category {
        Antique,
        Vintage,
        Classic,
        Sports,
        Luxury
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus productStatus = ProductStatus.PENDING;

    public enum ProductStatus {
        PENDING,
        APPROVED,
        DECLINED
    }

    @Version
    @Column(name = "version")
    private int version;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
    @Column(name = "created_by")
    private Integer createdBy;
    @Column(name = "updated_by")
    private Integer updatedBy;
}