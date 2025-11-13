package com.onepiece.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ProductRequestDTO {
    @NotNull(message = "Seller ID is required")
    @Positive(message = "Seller ID must be positive")
    private Integer sellerId;

    @NotBlank(message = "Product model is required")
    private String productModel;

    @NotNull(message = "Model year is required")
    @Positive(message = "Model year must be positive")
    private Integer modelYear;

    @NotNull(message = "Start price is required")
    @Positive(message = "Start price must be positive")
    private Integer startPrice;

    @NotNull(message = "Price jump is required")
    @Positive(message = "Price jump must be positive")
    private Integer priceJump;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Auction date is required")
    private LocalDate auctionDate;

    @NotNull(message = "Auction start time is required")
    private LocalTime auctionStartTime;

    @NotNull(message = "Auction duration is required")
    private LocalTime auctionDuration;

    @NotNull(message = "Category is required")
    private String category;

}