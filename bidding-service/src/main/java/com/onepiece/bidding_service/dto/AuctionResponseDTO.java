package com.onepiece.bidding_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionResponseDTO {
    private int auctionId;
    private int productId;
    private int currPrice;
    private String currStatus;
    private int bidCount;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
