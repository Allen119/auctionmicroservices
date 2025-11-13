package com.onepiece.bidding_service.mapper;

import com.onepiece.bidding_service.dto.AuctionRequestDTO;
import com.onepiece.bidding_service.dto.AuctionResponseDTO;
import com.onepiece.bidding_service.model.Auction;
import org.springframework.stereotype.Component;

@Component
public class AuctionMapper {

    public Auction toEntity(AuctionRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Auction auction = new Auction();
        auction.setProductId(dto.getProductId());
        auction.setPriceJump(dto.getPriceJump());
        auction.setCurrPrice(dto.getStartingPrice());
        auction.setBidCount(dto.getBidCount());
        auction.setCreatedBy(dto.getSellerId());
        auction.setUpdatedBy(dto.getSellerId());

        // Handle status
        if (dto.getCurrStatus() != null && !dto.getCurrStatus().isBlank()) {
            try {
                Auction.currStatus status = Auction.currStatus.valueOf(dto.getCurrStatus().toUpperCase());
                auction.setCurrStatus(status);
            } catch (IllegalArgumentException e) {
                auction.setCurrStatus(Auction.currStatus.SCHEDULED);  // Default to SCHEDULED
            }
        } else {
            auction.setCurrStatus(Auction.currStatus.SCHEDULED);      // ✅ DEFAULT if null
        }

        return auction;
    }

    public AuctionResponseDTO toResponseDTO(Auction auction) {
        if (auction == null) {
            return null;
        }

        return AuctionResponseDTO.builder()
                .auctionId(auction.getAuctionId())
                .productId(auction.getProductId())
                .currPrice(auction.getCurrPrice())
                .priceJump(auction.getPriceJump())
                .currStatus(auction.getCurrStatus() != null ? auction.getCurrStatus().name() : null)
                .bidCount(auction.getBidCount())
                .createdAt(auction.getCreatedAt())
                .updatedAt(auction.getUpdatedAt())
                .createdBy(auction.getCreatedBy())
                .updatedBy(auction.getUpdatedBy())
                .build();
    }
}