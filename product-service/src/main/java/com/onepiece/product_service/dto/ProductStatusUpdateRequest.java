package com.onepiece.product_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductStatusUpdateRequest {

    @NotNull(message = "Admin ID is required")
    @Positive(message = "Admin ID must be positive")
    private int adminId;

    private String status;
}

