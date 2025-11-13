package com.onepiece.bidding_service.mapper;

import com.onepiece.bidding_service.dto.BiddingRequestDTO;
import com.onepiece.bidding_service.dto.BiddingResponseDTO;
import com.onepiece.bidding_service.dto.PlaceBidRequestDTO;
import com.onepiece.bidding_service.model.Bidding;
import org.springframework.stereotype.Component;

@Component
public class BiddingMapper {

    public Bidding toEntity(BiddingRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Bidding bidding = new Bidding();
        bidding.setAuctionId(dto.getAuctionId());
        bidding.setBuyerId(dto.getBuyerId());
        bidding.setNewBidAmount(dto.getNewBidAmount());

        return bidding;
    }

    public Bidding toEntity(PlaceBidRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Bidding bidding = new Bidding();
        bidding.setAuctionId(dto.getAuctionId());
        bidding.setBuyerId(dto.getBuyerId());
        bidding.setNewBidAmount(dto.getBidAmount());

        return bidding;
    }

    public BiddingResponseDTO toResponseDTO(Bidding bidding) {
        if (bidding == null) {
            return null;
        }

        return BiddingResponseDTO.builder()
                .bidId(bidding.getBidId())
                .auctionId(bidding.getAuctionId())
                .buyerId(bidding.getBuyerId())
                .newBidAmount(bidding.getNewBidAmount())
                .bidTime(bidding.getBidTime())
                .createdAt(bidding.getCreatedAt())
                .updatedAt(bidding.getUpdatedAt())
                .createdBy(bidding.getCreatedBy())
                .updatedBy(bidding.getUpdatedBy())
                .build();
    }
}