package com.onepiece.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRequestDTO {
    private Integer productId;
    private Integer startingPrice;
    private Integer priceJump;
    private Integer sellerId;
    private Integer bidCount;
    private String currStatus;
}