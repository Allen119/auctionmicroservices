package com.onepiece.product_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ProductDTO {
    private int productId;
    private int sellerId;
    private String productModel;
    private int modelYear;
    private int startPrice;
    private int priceJump;
    private String description;
    private LocalDate auctionDate;
    private LocalTime auctionStartTime;
    private int auctionDuration;
    private String mainImageUrl;
    private List<String> additionalImageUrls;
    private String category;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
