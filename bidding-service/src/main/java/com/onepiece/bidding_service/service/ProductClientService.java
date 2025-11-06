package com.onepiece.bidding_service.service;


import com.onepiece.bidding_service.dto.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:3000/api/v1")
public interface ProductClientService {

    @GetMapping("/product/{productId}")
    ProductResponseDTO getProductById(@PathVariable int productId);
}
