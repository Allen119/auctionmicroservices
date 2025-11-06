package com.onepiece.bidding_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuctionDTO {
    private int auctionId;
    private int productId;
    private int currPrice;
    private String currStatus;
    private int bidCount;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
