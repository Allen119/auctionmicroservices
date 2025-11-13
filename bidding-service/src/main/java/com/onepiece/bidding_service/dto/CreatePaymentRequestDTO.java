package com.onepiece.bidding_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequestDTO {
    private Integer buyerId;
    private Integer sellerId;
    private Integer productId;
    private Integer auctionId;
    private Integer finalAmount;
}