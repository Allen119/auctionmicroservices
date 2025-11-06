package com.onepiece.reviewservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewRequestDTO {


    private Integer id;

    @NotNull(message = "Buyer ID is required")
    private Integer buyerId;

    @NotNull(message = "Seller id required")
    private Integer sellerId;

    @NotNull(message = "Auction id is required")
    private Integer auctionId;


    @Size(max = 1000, message = "review cannot exceed 1000 characters")
    @NotBlank(message = "Rating can't be blank")
    private String review;

    @Min(value = 1, message = "Minimum 1")
    @Max(value = 5, message = "Maximum 5")
    @NotNull(message = "Rating is required")
    private Float rating;

    private Long version;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer createdBy;

    private Integer updatedBy;
}
