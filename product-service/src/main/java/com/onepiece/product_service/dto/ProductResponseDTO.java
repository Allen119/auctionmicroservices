package com.onepiece.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
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
    private String category;
    private String mainImageUrl;
    private List<String> additionalImageUrls;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
