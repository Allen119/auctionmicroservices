package com.onepiece.product_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:3000/api/v1/users")
public interface UserClientService {

    @GetMapping("/check-seller-role/{sellerId}")
    Boolean checkSellerRole(@PathVariable("sellerId") int sellerId);
}
