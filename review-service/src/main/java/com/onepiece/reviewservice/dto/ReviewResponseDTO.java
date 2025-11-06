package com.onepiece.reviewservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponseDTO {

    private Integer id;
    private Integer buyerId;
    private Integer sellerId;
    private Integer auctionId;
    private String review;
    private Float rating;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public ReviewResponseDTO(Integer id, Integer buyerId, Integer sellerId, Integer auctionId,
                             String review, Float rating, Instant createdAt, Instant updatedAt,
                             Integer createdBy, Integer updatedBy) {
        this.id = id;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.auctionId = auctionId;
        this.review = review;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}