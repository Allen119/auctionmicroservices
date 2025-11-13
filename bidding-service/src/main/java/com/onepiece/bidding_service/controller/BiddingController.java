package com.onepiece.bidding_service.controller;

import com.onepiece.bidding_service.dto.BiddingRequestDTO;
import com.onepiece.bidding_service.dto.BiddingResponseDTO;
import com.onepiece.bidding_service.dto.PlaceBidRequestDTO;
import com.onepiece.bidding_service.service.BiddingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bidding-service")
@CrossOrigin
public class BiddingController {

    @Autowired
    private BiddingService biddingService;

    @GetMapping("/bids")
    public ResponseEntity<List<BiddingResponseDTO>> getAllBids() {
        List<BiddingResponseDTO> bids = biddingService.getAllBids();
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @PostMapping("/bids/add")
    public ResponseEntity<BiddingResponseDTO> addBidding(@Valid @RequestBody BiddingRequestDTO biddingDTO) {
        BiddingResponseDTO savedBid = biddingService.addBidding(biddingDTO);
        return new ResponseEntity<>(savedBid, HttpStatus.CREATED);
    }

    @PostMapping("/bids/place-bid")
    public ResponseEntity<BiddingResponseDTO> placeBid(@Valid @RequestBody PlaceBidRequestDTO placeBidRequest) {
        BiddingResponseDTO savedBid = biddingService.placeBid(placeBidRequest);
        return new ResponseEntity<>(savedBid, HttpStatus.CREATED);
    }

    @GetMapping("/bid/{bidId}")
    public ResponseEntity<BiddingResponseDTO> getBidById(@PathVariable int bidId) {
        BiddingResponseDTO bid = biddingService.getBidById(bidId);
        return new ResponseEntity<>(bid, HttpStatus.OK);
    }

    @DeleteMapping("/bid/{bidId}")
    public ResponseEntity<String> deleteBid(@PathVariable int bidId) {
        biddingService.deleteBidById(bidId);
        return new ResponseEntity<>("Bid deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/bids/auction/{auctionId}")
    public ResponseEntity<List<BiddingResponseDTO>> getBidsByAuctionId(@PathVariable int auctionId) {
        List<BiddingResponseDTO> bids = biddingService.getBidsByAuctionId(auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/bids/buyer/{buyerId}")
    public ResponseEntity<List<BiddingResponseDTO>> getBidsByBuyerId(@PathVariable int buyerId) {
        List<BiddingResponseDTO> bids = biddingService.getBidsByBuyerId(buyerId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/bids/auction/{auctionId}/highest")
    public ResponseEntity<BiddingResponseDTO> getHighestBidForAuction(@PathVariable int auctionId) {
        BiddingResponseDTO highestBid = biddingService.getHighestBidForAuction(auctionId);
        return new ResponseEntity<>(highestBid, HttpStatus.OK);
    }

    @GetMapping("/bids/auction/{auctionId}/count")
    public ResponseEntity<Long> getBidCountForAuction(@PathVariable int auctionId) {
        long count = biddingService.getBidCountForAuction(auctionId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}