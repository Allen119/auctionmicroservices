package com.onepiece.product_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductStatusUpdateDTO {
    @NotNull(message = "Product status is required")
    private String productStatus;  // APPROVED or DECLINED
}