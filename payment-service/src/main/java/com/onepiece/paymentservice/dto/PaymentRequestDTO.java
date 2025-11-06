package com.onepiece.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequestDTO {
    private Integer id;

    @NotNull(message = "Buyer ID is required")
    @Positive(message = "Buyer ID must be a positive integer")
    private Integer buyerId;

    @NotNull(message = "Seller id required")
    @Positive(message = "Seller ID must be a positive integer")
    private Integer sellerId;

    @NotBlank(message = "Transaction ID cannot be blank")
    @Size(min = 5, max = 50, message = "Transaction ID size invalid")
    private String transactionId;

    @NotNull(message = "Product id is required")
    @Positive(message = "Product ID must be a positive integer")
    private Integer productId;

    @NotNull(message = "Auction id is required")
    @Positive(message = "Auction ID must be a positive integer")
    private Integer auctionId;

    @NotNull(message = "final amount cannot be null")
    @Positive(message = "Final amount must be positive")
    private Integer finalAmount;

    @NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod;

    @NotBlank(message = "Transaction status cannot be blank")
    private String transactionStatus;

    private LocalDateTime paymentTime;

    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
}