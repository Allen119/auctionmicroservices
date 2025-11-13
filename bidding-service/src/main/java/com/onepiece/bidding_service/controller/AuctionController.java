package com.onepiece.bidding_service.controller;

import com.onepiece.bidding_service.dto.AuctionRequestDTO;
import com.onepiece.bidding_service.dto.AuctionResponseDTO;
import com.onepiece.bidding_service.service.AuctionCompletionService;
import com.onepiece.bidding_service.service.AuctionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bidding-service")
@CrossOrigin
@Slf4j
public class AuctionController {

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private AuctionCompletionService auctionCompletionService;


    @GetMapping("/auctions")
    public ResponseEntity<List<AuctionResponseDTO>> getAllAuctions() {
        List<AuctionResponseDTO> auctions = auctionService.getAllAuctions();
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }

    @PostMapping("/auctions/create-auction")
    public ResponseEntity<AuctionResponseDTO> createAuction(@Valid @RequestBody AuctionRequestDTO auctionDTO) {
        AuctionResponseDTO savedAuction = auctionService.createAuction(auctionDTO);
        return new ResponseEntity<>(savedAuction, HttpStatus.CREATED);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<AuctionResponseDTO> getAuctionDetailsById(@PathVariable int auctionId) {
        AuctionResponseDTO auction = auctionService.getAuctionById(auctionId);
        return new ResponseEntity<>(auction, HttpStatus.OK);
    }


    @PutMapping("/auction/{auctionId}")
    public ResponseEntity<AuctionResponseDTO> updateAuctionById(@PathVariable int auctionId,
                                                                @Valid @RequestBody AuctionRequestDTO auctionDTO) {
        AuctionResponseDTO updatedAuction = auctionService.updateAuctionById(auctionId, auctionDTO);
        return new ResponseEntity<>(updatedAuction, HttpStatus.OK);
    }


    @DeleteMapping("/auction/{auctionId}")
    public ResponseEntity<String> deleteAuction(@PathVariable int auctionId) {
        auctionService.deleteAuction(auctionId);
        return new ResponseEntity<>("Auction deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/auctions/product/{productId}")
    public ResponseEntity<List<AuctionResponseDTO>> getAuctionsByProductId(@PathVariable int productId) {
        List<AuctionResponseDTO> auctions = auctionService.getAuctionsByProductId(productId);
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }

    @GetMapping("/auctions/status/{status}")
    public ResponseEntity<List<AuctionResponseDTO>> getAuctionsByStatus(@PathVariable String status) {
        List<AuctionResponseDTO> auctions = auctionService.getAuctionsByStatus(status);
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }

    @GetMapping("/auctions/seller/{userId}")
    public ResponseEntity<List<AuctionResponseDTO>> getAuctionsByUserId(@PathVariable int userId) {
        List<AuctionResponseDTO> auctions = auctionService.getAuctionsByUserId(userId);
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }
}