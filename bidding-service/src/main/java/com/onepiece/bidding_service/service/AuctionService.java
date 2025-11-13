package com.onepiece.bidding_service.service;

import com.onepiece.bidding_service.dto.AuctionRequestDTO;
import com.onepiece.bidding_service.dto.AuctionResponseDTO;
import com.onepiece.bidding_service.mapper.AuctionMapper;
import com.onepiece.bidding_service.model.Auction;
import com.onepiece.bidding_service.repo.AuctionRepo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuctionService {

    @Autowired
    private AuctionRepo auctionRepo;

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private AuctionCompletionService auctionCompletionService;
    /*** Get all auctions
     */
    public List<AuctionResponseDTO> getAllAuctions() {
        List<Auction> auctions = auctionRepo.findAll();
        return auctions.stream()
                .map(auctionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new auction
     *
     * Data flow:
     * - startingPrice (from request) → currPrice (in DB)
     * - bidCount initialized to 0
     * - currStatus defaults to SCHEDULED if not provided
     * - createdAt/updatedAt handled by database
     * - createdBy/updatedBy set to sellerId
     *
     */
    public AuctionResponseDTO createAuction(AuctionRequestDTO auctionDTO) {

        if (auctionDTO.getStartingPrice() <= 0) {
            throw new IllegalArgumentException("Starting price must be positive");
        }

        if (auctionDTO.getPriceJump() <= 0) {
            throw new IllegalArgumentException("Price jump must be positive");
        }

        List<Auction> existingAuctions = auctionRepo.findByProductId(auctionDTO.getProductId());
        if (!existingAuctions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Auction already exists for product ID: " + auctionDTO.getProductId());
        }

        // Create auction from DTO
        Auction auction = auctionMapper.toEntity(auctionDTO);

        // Set default status if not provided
        if (auction.getCurrStatus() == null) {
            auction.setCurrStatus(Auction.currStatus.SCHEDULED);
        }

        auction.setCurrPrice(auctionDTO.getStartingPrice());  // startingPrice → currPrice
        auction.setBidCount(0);                                // Initialize bidCount to 0

        // Set seller as creator and updater
        auction.setCreatedBy(auctionDTO.getSellerId());
        auction.setUpdatedBy(auctionDTO.getSellerId());

        Auction savedAuction = auctionRepo.save(auction);
        return auctionMapper.toResponseDTO(savedAuction);
    }

    /**
     * Get auction by ID
     */
    public AuctionResponseDTO getAuctionById(int auctionId) {
        Auction auction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with ID: " + auctionId));
        return auctionMapper.toResponseDTO(auction);
    }

    /**
     * Update auction by ID
     */
    public AuctionResponseDTO updateAuctionById(int auctionId, @Valid AuctionRequestDTO auctionDTO) {

        Auction existingAuction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with ID: " + auctionId));

        // Update status if provided
        if (auctionDTO.getCurrStatus() != null && !auctionDTO.getCurrStatus().isBlank()) {
            try {
                Auction.currStatus status = Auction.currStatus.valueOf(auctionDTO.getCurrStatus().toUpperCase());
                existingAuction.setCurrStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + auctionDTO.getCurrStatus() +
                        ". Valid statuses are: SCHEDULED, PENDING, ONGOING, COMPLETED, TERMINATED");
            }
        }

        // Update price jump if provided
        if (auctionDTO.getPriceJump() > 0) {
            existingAuction.setPriceJump(auctionDTO.getPriceJump());
        }

        // Update starting price (currPrice) if provided
        if (auctionDTO.getStartingPrice() > 0) {
            existingAuction.setCurrPrice(auctionDTO.getStartingPrice());
        }

        // Update seller who made the change
        if (auctionDTO.getSellerId() > 0) {
            existingAuction.setUpdatedBy(auctionDTO.getSellerId());
        }

        Auction updatedAuction = auctionRepo.save(existingAuction);

        if (auctionDTO.getCurrStatus() != null &&
                auctionDTO.getCurrStatus().toUpperCase().equals("COMPLETED")) {

            log.info("🎯 Auction {} status changed to COMPLETED - Triggering ISC", auctionId);
            try {
                auctionCompletionService.handleAuctionCompletion(auctionId);
            } catch (Exception e) {
                log.warn("⚠️ ISC warning (non-blocking): {}", e.getMessage());
            }
        }

        return auctionMapper.toResponseDTO(updatedAuction);
    }
    /**
     * Delete auction by ID
     */
    public void deleteAuction(int auctionId) {
        if (!auctionRepo.existsById(auctionId)) {
            throw new RuntimeException("Auction not found with ID: " + auctionId);
        }
        auctionRepo.deleteById(auctionId);
    }

    /**
     * Get auctions by product ID
     */
    public List<AuctionResponseDTO> getAuctionsByProductId(int productId) {
        List<Auction> auctions = auctionRepo.findByProductId(productId);
        return auctions.stream()
                .map(auctionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get auctions by status
     */
    public List<AuctionResponseDTO> getAuctionsByStatus(String status) {
        try {
            Auction.currStatus statusEnum = Auction.currStatus.valueOf(status.toUpperCase());
            List<Auction> auctions = auctionRepo.findByCurrStatus(statusEnum);
            return auctions.stream()
                    .map(auctionMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status +
                    ". Valid statuses are: SCHEDULED, PENDING, ONGOING, COMPLETED, TERMINATED");
        }
    }

    /**
     * Get auctions by seller ID
     */
    public List<AuctionResponseDTO> getAuctionsByUserId(int userId) {
        List<Auction> auctions = auctionRepo.findAll();
        return auctions.stream()
                .filter(auction -> auction.getCreatedBy() == userId)
                .map(auctionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}