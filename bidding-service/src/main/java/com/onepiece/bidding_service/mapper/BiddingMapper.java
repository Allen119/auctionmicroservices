package com.onepiece.bidding_service.mapper;


import com.onepiece.bidding_service.dto.BiddingRequestDTO;
import com.onepiece.bidding_service.dto.BiddingResponseDTO;
import com.onepiece.bidding_service.model.Bidding;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BiddingMapper {

    public Bidding toEntity(BiddingRequestDTO dto) {
        Bidding bidding = new Bidding();
        bidding.setAuctionId(dto.getAuctionId());
        bidding.setBuyerId(dto.getBuyerId());
        bidding.setNewBidAmount(dto.getNewBidAmount());
        bidding.setBidTime(LocalDateTime.now());
        return bidding;
    }

    public BiddingResponseDTO toResponseDTO(Bidding bidding) {
        return BiddingResponseDTO.builder()
                .bidId(bidding.getBidId())
                .auctionId(bidding.getAuctionId())
                .buyerId(bidding.getBuyerId())
                .newBidAmount(bidding.getNewBidAmount())
                .bidTime(bidding.getBidTime())
                .createdAt(bidding.getCreatedAt())
                .updatedAt(bidding.getUpdatedAt())
                .build();
    }
}

