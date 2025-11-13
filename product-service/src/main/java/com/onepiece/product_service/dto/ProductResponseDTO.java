package com.onepiece.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Integer productId;
    private Integer sellerId;
    private String productModel;
    private Integer modelYear;
    private Integer startPrice;
    private Integer priceJump;
    private String description;
    private LocalDate auctionDate;
    private LocalTime auctionStartTime;
    private LocalTime auctionDuration;
    private String category;
    private String productStatus;
    private List<String> imageUrls;
}