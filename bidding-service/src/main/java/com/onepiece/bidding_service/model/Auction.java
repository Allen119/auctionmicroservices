package com.onepiece.bidding_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private int auctionId;

    @Column(name = "product_id", nullable = false, unique = true)
    private int productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "curr_status", nullable = false)
    private currStatus currStatus;

    @Column(name = "price_jump", nullable = false)
    private int priceJump;

    @Column(name = "curr_price", nullable = false)
    private int currPrice;

    @Column(name = "bid_count", nullable = false)
    private int bidCount;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false,
            insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "created_by",updatable = false)
    private int createdBy;

    @Column(name = "updated_by")
    private int updatedBy;

    // Enum for status
    public enum currStatus {
        COMPLETED,
        PENDING,
        SCHEDULED,
        ONGOING,
        TERMINATED
    }
}