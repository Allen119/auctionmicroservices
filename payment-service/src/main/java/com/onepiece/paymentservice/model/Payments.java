package com.onepiece.paymentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payments {
    public Payments(Integer buyerId, Integer sellerId, String transactionId, Integer productId,
                    Integer auctionId, Integer finalAmount, String paymentMethod, String transactionStatus,
                    Integer createdBy, Integer updatedBy) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.auctionId = auctionId;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionStatus = transactionStatus;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }


    public void updateStatusDetails(
            String transactionId,
            String paymentMethod,
            Integer finalAmount,
            String transactionStatus,
            Integer updatedBy
    ) {
        if (transactionId != null) {
            this.transactionId = transactionId;
        }
        if (paymentMethod != null) {
            this.paymentMethod = paymentMethod;
        }
        if (finalAmount != null) {
            this.finalAmount = finalAmount;
        }
        if (transactionStatus != null) {
            this.transactionStatus = transactionStatus;
        }
        if (updatedBy != null) {
            this.updatedBy = updatedBy;
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer id;

    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "auction_id", nullable = false)
    private Integer auctionId;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Version
    @Column(name = "version")
    private Long version = 1L;

    @Column(name = "created_at",updatable = false,insertable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;
}
