package com.onepiece.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ProductRequestDTO {
    
    @NotNull(message = "Seller ID is required")
    @Positive(message = "Seller ID must be positive")
    private int sellerId;
    
    @NotBlank(message = "Product model is required")
    private String productModel;
    
    @NotNull(message = "Model year is required")
    @Positive(message = "Model year must be positive")
    private int modelYear;
    
    @NotNull(message = "Start price is required")
    @Positive(message = "Start price must be positive")
    private int startPrice;

    @NotNull(message = "Price jump is required")
    @Positive(message = "Price Jump should be postive")
    private int priceJump;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Auction date is required")
    private LocalDate auctionDate;
    
    @NotNull(message = "Auction start time is required")
    private LocalTime auctionStartTime;
    
    @NotNull(message = "Auction duration is required")
    @Positive(message = "Auction duration must be positive")
    private int auctionDuration;
    
    @NotNull(message = "Category is required")
    private String category;
}
