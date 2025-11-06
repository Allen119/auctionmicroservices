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
public class BiddingResponseDTO {
    private int bidId;
    private int auctionId;
    private int buyerId;
    private int newBidAmount;
    private LocalDateTime bidTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

