package com.onepiece.product_service.controller;

import com.onepiece.product_service.dto.ProductResponseDTO;
import com.onepiece.product_service.dto.ProductStatusUpdateDTO;
import com.onepiece.product_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-service/admin")
@CrossOrigin
@Slf4j
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @PutMapping("/products/{productId}/status")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable int productId,
            @RequestBody ProductStatusUpdateDTO statusUpdate) {
        try {
            ProductResponseDTO updatedProduct = productService.updateProductStatus(productId, statusUpdate);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable int productId) {
        try {
            boolean deleted = productService.deleteProduct(productId);
            if (deleted) {
                return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}