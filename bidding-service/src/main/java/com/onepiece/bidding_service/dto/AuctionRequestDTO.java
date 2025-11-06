package com.onepiece.bidding_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AuctionRequestDTO {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private int productId;

    @NotNull(message = "Current price is required")
    @Positive(message = "Current price must be positive")
    private int currPrice;

    @NotNull(message = "Current status is required")
    private String currStatus;


    private int bidCount;

}
