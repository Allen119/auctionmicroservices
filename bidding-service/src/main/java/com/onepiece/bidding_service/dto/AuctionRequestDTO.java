package com.onepiece.bidding_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuctionRequestDTO {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private int productId;

    @NotNull(message = "Starting price is required")
    @Positive(message = "Starting price must be positive")
    private int startingPrice;

    @NotNull(message = "Price jump is required")
    @Positive(message = "Price jump must be positive")
    private int priceJump;

    @NotNull(message = "Seller ID is required")
    @Positive(message = "Seller ID must be positive")
    private int sellerId;

    private String currStatus;

    private int currPrice;
    private int bidCount = 0;

    public AuctionRequestDTO(int productId, int startingPrice, int priceJump,
                             int sellerId, String currStatus) {
        this.productId = productId;
        this.startingPrice = startingPrice;
        this.priceJump = priceJump;
        this.sellerId = sellerId;
        this.currStatus = currStatus;
        this.currPrice = startingPrice;
        this.bidCount = 0;
    }
}