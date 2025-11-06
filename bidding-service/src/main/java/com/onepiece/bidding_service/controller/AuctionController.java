package com.onepiece.bidding_service.controller;


import com.onepiece.bidding_service.dto.AuctionRequestDTO;
import com.onepiece.bidding_service.dto.AuctionResponseDTO;
import com.onepiece.bidding_service.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class    AuctionController {

    @Autowired
    private AuctionService auctionService;

    @GetMapping("/auctions")
    public ResponseEntity<?> getAllAuctions(){
        try {
            System.out.println("Controller method called");
            List<AuctionResponseDTO> auctions = auctionService.getAllAuctions();
            System.out.println("Auctions retrieved: " + auctions.size());
            return new ResponseEntity<>(auctions, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error in getAllAuctions: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/auctions")
    public ResponseEntity<?> createAuction(@Valid @RequestBody AuctionRequestDTO auction){
        AuctionResponseDTO savedAuction = null;

        if(auction.getCurrPrice() == 0 || auction.getCurrStatus() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            savedAuction = auctionService.createAuction(auction);
            return new ResponseEntity<>(savedAuction, HttpStatus.OK);
        } catch (IOException e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<AuctionResponseDTO> getAuctionDetailsById(@PathVariable int auctionId){
        try {
            AuctionResponseDTO auction = auctionService.getAuctionById(auctionId);
            return new ResponseEntity<>(auction, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/auction/{auctionId}")
    public ResponseEntity<?> updateAuctionById(@PathVariable int auctionId, @Valid @RequestBody AuctionRequestDTO auctionDTO){
        try{
            AuctionResponseDTO updatedAuction = auctionService.updateAuctionById(auctionId, auctionDTO);
            return new ResponseEntity<>(updatedAuction, HttpStatus.OK);
        } catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/auction/{auctionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteAuction(@PathVariable int auctionId){
        try {
            auctionService.deleteAuction(auctionId);
            return new ResponseEntity<>("Auction deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/auctions/product/{productId}")
    public ResponseEntity<List<AuctionResponseDTO>> getAuctionsByProductId(@PathVariable int productId){
        try {
            List<AuctionResponseDTO> auctions = auctionService.getAuctionsByProductId(productId);
            return new ResponseEntity<>(auctions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auctions/status/{status}")
    public ResponseEntity<?> getAuctionsByStatus(@PathVariable String status){
        try {
            List<AuctionResponseDTO> auctions = auctionService.getAuctionsByStatus(status);
            return new ResponseEntity<>(auctions, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
