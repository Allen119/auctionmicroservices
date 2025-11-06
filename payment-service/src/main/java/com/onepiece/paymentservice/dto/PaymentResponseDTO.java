package com.onepiece.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponseDTO {

    private Integer id;
    private Integer buyerId;
    private Integer sellerId;
    private String transactionId;
    private Integer productId;
    private Integer auctionId;
    private Integer finalAmount;
    private String paymentMethod;
    private String transactionStatus;
    private LocalDateTime paymentTime;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer createdBy;
    private Integer updatedBy;


    public PaymentResponseDTO(Integer id, Integer buyerId, Integer sellerId, String transactionId,
                              Integer productId, Integer auctionId, Integer finalAmount,
                              String paymentMethod, String transactionStatus,
                              LocalDateTime paymentTime, Instant createdAt, Instant updatedAt,
                              Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.auctionId = auctionId;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionStatus = transactionStatus;
        this.paymentTime = paymentTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}