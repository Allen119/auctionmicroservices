package com.onepiece.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ DTO received from Bidding Service (ISC)
 * Minimal fields for ISC payment creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentFromBiddingDTO {

    @NotNull(message = "Buyer ID is required")
    @Positive(message = "Buyer ID must be positive")
    private Integer buyerId;

    @NotNull(message = "Seller ID is required")
    @Positive(message = "Seller ID must be positive")
    private Integer sellerId;

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Integer productId;

    @NotNull(message = "Auction ID is required")
    @Positive(message = "Auction ID must be positive")
    private Integer auctionId;

    @NotNull(message = "Final amount is required")
    @Positive(message = "Final amount must be positive")
    private Integer finalAmount;
}