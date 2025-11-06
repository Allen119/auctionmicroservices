package com.onepiece.product_service.service;

import com.onepiece.product_service.dto.AuctionResponseDTO;
import com.onepiece.product_service.dto.BiddingResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "bidding-service", url = "http://localhost:3000/api/v1")
public interface AuctionClientService {

    @GetMapping("/auctions/product/{productId}")
    List<AuctionResponseDTO> getAuctionsByProductId(@PathVariable("productId") int productId);

    @DeleteMapping("/auction/{auctionId}")
    void deleteAuction(@PathVariable int auctionId);

    @GetMapping("/bids/auction/{auctionId}")
    List<BiddingResponseDTO> getBidsByAuctionId(@PathVariable int auctionId);

    @DeleteMapping("/bid/{bidId}")
    void deleteBidById(@PathVariable int bidId);


}
