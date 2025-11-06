package com.onepiece.bidding_service.service;



import com.onepiece.bidding_service.dto.AuctionRequestDTO;
import com.onepiece.bidding_service.dto.AuctionResponseDTO;
import com.onepiece.bidding_service.dto.ProductResponseDTO;
import com.onepiece.bidding_service.mapper.AuctionMapper;
import com.onepiece.bidding_service.model.Auction;
import com.onepiece.bidding_service.repo.AuctionRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepo auctionRepo;

    @Autowired
    private AuctionMapper auctionMapper;

    @Autowired
    private ProductClientService productClientService;


    public List<AuctionResponseDTO> getAllAuctions() {
        List<Auction> auctions = auctionRepo.findAll();
        return auctions.stream()
                .map(auction -> auctionMapper.toResponseDTO(auction))
                .collect(Collectors.toList());
    }

    public AuctionResponseDTO createAuction(@Valid AuctionRequestDTO auctionDTO) throws IOException {
        Auction auction = auctionMapper.toEntity(auctionDTO);
        
        auction.setCreatedAt(LocalDateTime.now());
        auction.setUpdatedAt(LocalDateTime.now());

        ProductResponseDTO product = productClientService.getProductById(auctionDTO.getProductId());
        
        Integer sellerId = product.getSellerId();
        if (sellerId == null) {
            throw new RuntimeException("Product does not have a valid seller ID");
        }

        System.out.println("Seller ID found: " + sellerId);
        auction.setCreatedBy(sellerId);
        auction.setUpdatedBy(sellerId);

        Auction savedAuction = auctionRepo.save(auction);
        return auctionMapper.toResponseDTO(savedAuction);
    }

    public AuctionResponseDTO getAuctionById(int auctionId) {
        Optional<Auction> auctionOpt = auctionRepo.findById(auctionId);
        if (auctionOpt.isPresent()) {
            return auctionMapper.toResponseDTO(auctionOpt.get());
        }
        throw new RuntimeException("Auction not found with ID: " + auctionId);
    }

    public AuctionResponseDTO updateAuctionById(int auctionId, AuctionRequestDTO auctionDTO) throws IOException{
        Auction existingAuction = auctionRepo.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found with ID: " + auctionId));

        if(auctionDTO.getCurrStatus() != null){
            try {
                Auction.CurrStatus status = Auction.CurrStatus.valueOf(auctionDTO.getCurrStatus().toUpperCase());
                existingAuction.setCurrStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + auctionDTO.getCurrStatus());
            }
        }
        if(auctionDTO.getCurrPrice() != 0){
            existingAuction.setCurrPrice(auctionDTO.getCurrPrice());
        }
        if(auctionDTO.getBidCount() != 0){
            existingAuction.setBidCount(auctionDTO.getBidCount());
        }
        
        existingAuction.setUpdatedAt(LocalDateTime.now());

        Auction updatedAuction = auctionRepo.save(existingAuction);
        return auctionMapper.toResponseDTO(updatedAuction);
    }

    public void deleteAuction(int auctionId) {
        if (!auctionRepo.existsById(auctionId)) {
            throw new RuntimeException("Auction not found with ID: " + auctionId);
        }
        auctionRepo.deleteById(auctionId);
    }

    public List<AuctionResponseDTO> getAuctionsByProductId(int productId) {
        List<Auction> auctions = auctionRepo.findByProductId(productId);
        return auctions.stream()
                .map(auction -> auctionMapper.toResponseDTO(auction))
                .collect(Collectors.toList());
    }

    public List<AuctionResponseDTO> getAuctionsByStatus(String status) {
        try {
            Auction.CurrStatus statusEnum = Auction.CurrStatus.valueOf(status.toUpperCase());
            List<Auction> auctions = auctionRepo.findByCurrStatus(statusEnum);
            return auctions.stream()
                    .map(auction -> auctionMapper.toResponseDTO(auction))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status + 
                ". Valid statuses are: COMPLETED, PENDING, SCHEDULED, ONGOING, TERMINATED");
        }
    }
}
