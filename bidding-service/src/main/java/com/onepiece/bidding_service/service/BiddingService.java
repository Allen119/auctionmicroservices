package com.onepiece.bidding_service.service;


import com.onepiece.bidding_service.dto.BiddingRequestDTO;
import com.onepiece.bidding_service.dto.BiddingResponseDTO;
import com.onepiece.bidding_service.dto.PlaceBidRequestDTO;
import com.onepiece.bidding_service.dto.ProductResponseDTO;
import com.onepiece.bidding_service.mapper.BiddingMapper;
import com.onepiece.bidding_service.model.Auction;
import com.onepiece.bidding_service.model.Bidding;
import com.onepiece.bidding_service.repo.AuctionRepo;
import com.onepiece.bidding_service.repo.BiddingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BiddingService {

    @Autowired
    private BiddingRepo biddingRepo;

    @Autowired
    private AuctionRepo auctionRepo;

    @Autowired
    private ProductClientService productClientService;

    @Autowired
    private BiddingMapper biddingMapper;

    public List<BiddingResponseDTO> getAllBids() {
        List<Bidding> bids = biddingRepo.findAll();
        return bids.stream()
                .map(bid -> biddingMapper.toResponseDTO(bid))
                .collect(Collectors.toList());
    }

    public BiddingResponseDTO addBidding(BiddingRequestDTO biddingDTO) throws IOException {
        Auction auction = auctionRepo.findById(biddingDTO.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found with ID: " + biddingDTO.getAuctionId()));

        if (auction.getCurrStatus() != Auction.CurrStatus.ONGOING) {
            throw new IllegalArgumentException("Cannot place bid. Auction status is: " + auction.getCurrStatus());
        }

        Bidding bidding = biddingMapper.toEntity(biddingDTO);
        bidding.setCreatedAt(LocalDateTime.now());
        bidding.setUpdatedAt(LocalDateTime.now());
        bidding.setCreatedBy(biddingDTO.getBuyerId());
        bidding.setUpdatedBy(biddingDTO.getBuyerId());

        Bidding savedBidding = biddingRepo.save(bidding);
        return biddingMapper.toResponseDTO(savedBidding);
    }

    public BiddingResponseDTO getBidById(int bidId) {
        Bidding bidding = biddingRepo.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found with ID: " + bidId));
        return biddingMapper.toResponseDTO(bidding);
    }

    public void deleteBidById(int bidId) {
        if (!biddingRepo.existsById(bidId)) {
            throw new RuntimeException("Bid not found with ID: " + bidId);
        }
        biddingRepo.deleteById(bidId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public BiddingResponseDTO placeBid(PlaceBidRequestDTO placeBidRequest) throws IOException {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                Auction auction = auctionRepo.findByIdWithLock(placeBidRequest.getAuctionId())
                        .orElseThrow(() -> new RuntimeException("Auction not found with ID: " + placeBidRequest.getAuctionId()));

                if (auction.getCurrStatus() != Auction.CurrStatus.ONGOING) {
                    throw new IllegalArgumentException("Cannot place bid. Auction status is: " + auction.getCurrStatus());
                }

                ProductResponseDTO product = productClientService.getProductById(auction.getProductId());

                // Validate bid amount
                int bidAmount = placeBidRequest.getBidAmount();
                int minimumBid = auction.getCurrPrice() + product.getPriceJump();

                if (bidAmount < minimumBid) {
                    throw new IllegalArgumentException(
                        "Bid amount must be at least " + minimumBid +
                        " (current price: " + auction.getCurrPrice() +
                        " + price jump: " + product.getPriceJump() + ")");
                }

                Bidding newBidding = new Bidding();
                newBidding.setAuctionId(placeBidRequest.getAuctionId());
                newBidding.setBuyerId(placeBidRequest.getBuyerId());
                newBidding.setNewBidAmount(bidAmount);
                newBidding.setBidTime(LocalDateTime.now());
                newBidding.setCreatedAt(LocalDateTime.now());
                newBidding.setUpdatedAt(LocalDateTime.now());
                newBidding.setCreatedBy(placeBidRequest.getBuyerId());
                newBidding.setUpdatedBy(placeBidRequest.getBuyerId());

                auction.setCurrPrice(bidAmount);
                auction.setBidCount(auction.getBidCount() + 1);
                auction.setUpdatedAt(LocalDateTime.now());
                auction.setUpdatedBy(placeBidRequest.getBuyerId());

                Bidding savedBidding = biddingRepo.save(newBidding);
                auctionRepo.save(auction);

                return biddingMapper.toResponseDTO(savedBidding);

            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new IOException("Failed to place bid after " + maxRetries +
                        " attempts. The auction is experiencing high bidding activity. Please try again.");
                }
                try {
                    Thread.sleep(100 * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Bid placement interrupted");
                }
            }
        }

        throw new IOException("Failed to place bid due to concurrent access");
    }

    public List<BiddingResponseDTO> getBidsByAuctionId(int auctionId) {
        List<Bidding> bids = biddingRepo.findByAuctionIdOrderByBidAmountDesc(auctionId);
        return bids.stream()
                .map(bid -> biddingMapper.toResponseDTO(bid))
                .collect(Collectors.toList());
    }

    public List<BiddingResponseDTO> getBidsByBuyerId(int buyerId) {
        List<Bidding> bids = biddingRepo.findByBuyerId(buyerId);
        return bids.stream()
                .map(bid -> biddingMapper.toResponseDTO(bid))
                .collect(Collectors.toList());
    }
}
