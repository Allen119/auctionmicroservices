package com.onepiece.bidding_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceBidRequestDTO {

    @NotNull(message = "Auction ID is required")
    @Positive(message = "Auction ID must be positive")
    private int auctionId;

    @NotNull(message = "Buyer ID is required")
    @Positive(message = "Buyer ID must be positive")
    private int buyerId;

    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be positive")
    private int bidAmount;
}

