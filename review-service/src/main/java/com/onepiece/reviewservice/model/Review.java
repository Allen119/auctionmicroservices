package com.onepiece.reviewservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name="reviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    public Review(Integer buyerId, Integer sellerId, Integer auctionId, String review, Float rating, Integer createdBy, Integer updatedBy) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.auctionId = auctionId;
        this.review = review;
        this.rating = rating;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public Review(Integer id, Integer buyerId, Integer sellerId, Integer auctionId, String review, Float rating, Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.auctionId = auctionId;
        this.review = review;
        this.rating = rating;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer id;

    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @Column(name  = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "auction_id", nullable = false)
    private Integer auctionId;

    @Column(nullable = false, length = 1000)
    private String review;

    @Column(nullable = false)
    private Float rating;

    @Version
    @Column(name = "version")
    private Long version = 1L;


    @Column(name = "created_at", nullable = false, updatable = false,insertable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;

    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;
}
