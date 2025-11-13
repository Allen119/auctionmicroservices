package com.onepiece.bidding_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionResponseDTO {
    private int auctionId;
    private int productId;
    private int currPrice;
    private int priceJump;
    private String currStatus;
    private int bidCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int createdBy;
    private int updatedBy;
}