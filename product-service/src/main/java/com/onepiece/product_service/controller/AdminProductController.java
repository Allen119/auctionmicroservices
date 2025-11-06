package com.onepiece.product_service.controller;


import com.onepiece.product_service.dto.ProductResponseDTO;
import com.onepiece.product_service.dto.ProductStatusUpdateRequest;
import com.onepiece.product_service.model.Product;
import com.onepiece.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/pending")
    public ResponseEntity<?> getPendingProducts() {
        try {
            List<ProductResponseDTO> products = productService.getPendingProducts();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching pending products: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/products/status/{status}")
    public ResponseEntity<?> getProductsByStatus(@PathVariable String status) {
        try {
            Product.ProductStatus productStatus;
            try {
                productStatus = Product.ProductStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid status. Valid values are: PENDING, APPROVED, DECLINED",
                    HttpStatus.BAD_REQUEST);
            }

            List<ProductResponseDTO> products = productService.getProductsByStatus(productStatus);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching products: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/products/{productId}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> approveProduct(
            @PathVariable int productId,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        try {
            ProductResponseDTO approvedProduct = productService.approveProduct(productId, request.getAdminId());
            return new ResponseEntity<>(approvedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Product not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error approving product: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/products/{productId}/decline")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> declineProduct(
            @PathVariable int productId,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        try {
            ProductResponseDTO declinedProduct = productService.declineProduct(productId, request.getAdminId());
            return new ResponseEntity<>(declinedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Product not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error declining product: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/products/{productId}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable int productId,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        try {
            if (request.getStatus() == null || request.getStatus().isEmpty()) {
                return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
            }

            Product.ProductStatus productStatus;
            try {
                productStatus = Product.ProductStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid status. Valid values are: PENDING, APPROVED, DECLINED",
                    HttpStatus.BAD_REQUEST);
            }

            ProductResponseDTO updatedProduct = productService.updateProductStatus(
                productId, productStatus, request.getAdminId());
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Product not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating product status: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

