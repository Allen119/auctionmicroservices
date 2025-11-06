package com.onepiece.bidding_service.controller;


import com.onepiece.bidding_service.dto.BiddingRequestDTO;
import com.onepiece.bidding_service.dto.BiddingResponseDTO;
import com.onepiece.bidding_service.dto.PlaceBidRequestDTO;
import com.onepiece.bidding_service.service.BiddingService;
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
public class BiddingController {

    @Autowired
    private BiddingService biddingService;

    @GetMapping("/bids")
    public ResponseEntity<List<BiddingResponseDTO>> getAllBids(){
        try {
            List<BiddingResponseDTO> bids = biddingService.getAllBids();
            return new ResponseEntity<>(bids, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/bids")
    public ResponseEntity<?> createBidding(@Valid @RequestBody BiddingRequestDTO biddingDTO){
        try{
            BiddingResponseDTO newBid = biddingService.addBidding(biddingDTO);
            return new ResponseEntity<>(newBid, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bid/{bidId}")
    public ResponseEntity<?> getBidById(@PathVariable int bidId){
        try {
            BiddingResponseDTO bid = biddingService.getBidById(bidId);
            return new ResponseEntity<>(bid, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/bid/{bidId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBidById(@PathVariable int bidId){
        try {
            biddingService.deleteBidById(bidId);
            return new ResponseEntity<>("Bid deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/place-bid")
    public ResponseEntity<?> placeBid(@Valid @RequestBody PlaceBidRequestDTO placeBidRequest){
        try{
            BiddingResponseDTO updatedBid = biddingService.placeBid(placeBidRequest);
            return new ResponseEntity<>(updatedBid, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/bids/auction/{auctionId}")
    public ResponseEntity<?> getBidsByAuctionId(@PathVariable int auctionId){
        try {
            List<BiddingResponseDTO> bids = biddingService.getBidsByAuctionId(auctionId);
            return new ResponseEntity<>(bids, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/bids/buyer/{buyerId}")
    public ResponseEntity<?> getBidsByBuyerId(@PathVariable int buyerId){
        try {
            List<BiddingResponseDTO> bids = biddingService.getBidsByBuyerId(buyerId);
            return new ResponseEntity<>(bids, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}