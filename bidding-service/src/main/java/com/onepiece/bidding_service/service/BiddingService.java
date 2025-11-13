package com.onepiece.bidding_service.service;

import com.onepiece.bidding_service.dto.BiddingRequestDTO;
import com.onepiece.bidding_service.dto.BiddingResponseDTO;
import com.onepiece.bidding_service.dto.PlaceBidRequestDTO;
import com.onepiece.bidding_service.mapper.BiddingMapper;
import com.onepiece.bidding_service.model.Auction;
import com.onepiece.bidding_service.model.Bidding;
import com.onepiece.bidding_service.repo.AuctionRepo;
import com.onepiece.bidding_service.repo.BiddingRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
    private BiddingMapper biddingMapper;

    public List<BiddingResponseDTO> getAllBids() {
        List<Bidding> bids = biddingRepo.findAll();
        return bids.stream()
                .map(biddingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BiddingResponseDTO addBidding(@Valid BiddingRequestDTO biddingDTO) {  // ✅ FIXED: No throws IOException

        Auction auction = auctionRepo.findById(biddingDTO.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found with ID: " + biddingDTO.getAuctionId()));

        if (auction.getCurrStatus() != Auction.currStatus.ONGOING) {
            throw new IllegalArgumentException(
                    "Cannot place bid. Auction status is: " + auction.getCurrStatus());
        }

        int minimumBid = auction.getCurrPrice() + biddingDTO.getPriceJump();
        if (biddingDTO.getNewBidAmount() < minimumBid) {
            throw new IllegalArgumentException(
                    "Bid amount must be at least " + minimumBid +
                            " (current price: " + auction.getCurrPrice() +
                            " + price jump: " + biddingDTO.getPriceJump() + ")");
        }

        // Create bidding record
        Bidding bidding = biddingMapper.toEntity(biddingDTO);

        LocalDateTime now = LocalDateTime.now();
        bidding.setBidTime(now);
        bidding.setCreatedAt(now);
        bidding.setUpdatedAt(now);
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
    public BiddingResponseDTO placeBid(@Valid PlaceBidRequestDTO placeBidRequest) {  // ✅ FIXED: No throws IOException
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                Auction auction = auctionRepo.findByIdWithLock(placeBidRequest.getAuctionId())
                        .orElseThrow(() -> new RuntimeException(
                                "Auction not found with ID: " + placeBidRequest.getAuctionId()));

                if (auction.getCurrStatus() != Auction.currStatus.ONGOING) {
                    throw new IllegalArgumentException(
                            "Cannot place bid. Auction status is: " + auction.getCurrStatus());
                }

                int minimumBid = auction.getCurrPrice() + placeBidRequest.getPriceJump();
                if (placeBidRequest.getBidAmount() < minimumBid) {
                    throw new IllegalArgumentException(
                            "Bid amount must be at least " + minimumBid +
                                    " (current price: " + auction.getCurrPrice() +
                                    " + price jump: " + placeBidRequest.getPriceJump() + ")");
                }

                Bidding newBidding = new Bidding();
                newBidding.setAuctionId(placeBidRequest.getAuctionId());
                newBidding.setBuyerId(placeBidRequest.getBuyerId());
                newBidding.setNewBidAmount(placeBidRequest.getBidAmount());

                LocalDateTime now = LocalDateTime.now();
                newBidding.setBidTime(now);
                newBidding.setCreatedAt(now);
                newBidding.setUpdatedAt(now);
                newBidding.setCreatedBy(placeBidRequest.getBuyerId());
                newBidding.setUpdatedBy(placeBidRequest.getBuyerId());

                auction.setCurrPrice(placeBidRequest.getBidAmount());
                auction.setBidCount(auction.getBidCount() + 1);
                auction.setUpdatedAt(now);
                auction.setUpdatedBy(placeBidRequest.getBuyerId());

                // Save both records
                Bidding savedBidding = biddingRepo.save(newBidding);
                auctionRepo.save(auction);

                return biddingMapper.toResponseDTO(savedBidding);

            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new RuntimeException(
                            "Failed to place bid after " + maxRetries +
                                    " attempts. The auction is experiencing high bidding activity. Please try again.");
                }

                // Exponential backoff: 100ms, 200ms, 300ms
                try {
                    Thread.sleep(100L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Bid placement interrupted");
                }
            }
        }

        throw new RuntimeException("Failed to place bid due to concurrent access");
    }

    public List<BiddingResponseDTO> getBidsByAuctionId(int auctionId) {
        List<Bidding> bids = biddingRepo.findByAuctionIdOrderByNewBidAmountDesc(auctionId);  // ✅ FIXED
        return bids.stream()
                .map(biddingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<BiddingResponseDTO> getBidsByBuyerId(int buyerId) {
        List<Bidding> bids = biddingRepo.findByBuyerId(buyerId);
        return bids.stream()
                .map(biddingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    public BiddingResponseDTO getHighestBidForAuction(int auctionId) {
        Bidding bidding = biddingRepo.findHighestBidForAuction(auctionId);
        if (bidding == null) {
            throw new RuntimeException("No bids found for auction ID: " + auctionId);
        }
        return biddingMapper.toResponseDTO(bidding);
    }

    public long getBidCountForAuction(int auctionId) {
        return biddingRepo.countByAuctionId(auctionId);
    }
}