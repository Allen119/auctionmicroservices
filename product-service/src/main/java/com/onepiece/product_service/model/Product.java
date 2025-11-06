package com.onepiece.product_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

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
    private int productId;

    @Column(name = "seller_id")
    private Integer sellerId;

    @Column(name = "product_model")
    private String productModel;

    @Column(name = "model_year")
    private int modelYear;

    @Column(name = "start_price")
    private int startPrice;

    @Column(name = "price_jump")
    private int priceJump;

    @Column(name = "detail")
    private String description;

    @Column(name = "auction_date")
    private LocalDate auctionDate;

    @Column(name = "auction_start_time")
    private LocalTime auctionStartTime;

    @Column(name = "auction_duration")
    private int auctionDuration;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status")
    private ProductStatus productStatus;

    public enum ProductStatus{
        PENDING,
        APPROVED,
        DECLINED
    };

    @Column(name = "product_img", columnDefinition = "LONGBLOB")
    @Lob
    private byte[] productImg;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    public enum Category
    {
        Antique,
        Vintage,
        Classic,
        Sports,
        Luxury
    };

    @Version
    @Column(name = "version")
    private int version;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    private Integer createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Integer updatedBy;
}
