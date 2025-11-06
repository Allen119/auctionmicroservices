package com.onepiece.bidding_service.mapper;


import com.onepiece.bidding_service.dto.AuctionRequestDTO;
import com.onepiece.bidding_service.dto.AuctionResponseDTO;
import com.onepiece.bidding_service.model.Auction;
import org.springframework.stereotype.Component;

@Component
public class AuctionMapper {

    public Auction toEntity(AuctionRequestDTO dto) {
        Auction auction = new Auction();
        auction.setProductId(dto.getProductId());
        auction.setCurrPrice(dto.getCurrPrice());
        auction.setBidCount(dto.getBidCount());
        
        try {
            String statusInput = dto.getCurrStatus().toUpperCase();
            Auction.CurrStatus statusEnum;
            
            switch (statusInput) {
                case "COMPLETED":
                    statusEnum = Auction.CurrStatus.COMPLETED;
                    break;
                case "PENDING":
                    statusEnum = Auction.CurrStatus.PENDING;
                    break;
                case "SCHEDULED":
                    statusEnum = Auction.CurrStatus.SCHEDULED;
                    break;
                case "ONGOING":
                    statusEnum = Auction.CurrStatus.ONGOING;
                    break;
                case "TERMINATED":
                    statusEnum = Auction.CurrStatus.TERMINATED;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status: " + dto.getCurrStatus() + 
                        ". Valid statuses are: COMPLETED, PENDING, SCHEDULED, ONGOING, TERMINATED");
            }
            
            auction.setCurrStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + dto.getCurrStatus() + 
                ". Valid statuses are: COMPLETED, PENDING, SCHEDULED, ONGOING, TERMINATED");
        }
        
        return auction;
    }

    public AuctionResponseDTO toResponseDTO(Auction auction) {
        return AuctionResponseDTO.builder()
                .auctionId(auction.getAuctionId())
                .productId(auction.getProductId())
                .currPrice(auction.getCurrPrice())
                .currStatus(auction.getCurrStatus() != null ? auction.getCurrStatus().toString() : null)
                .bidCount(auction.getBidCount())
                .createdAt(auction.getCreatedAt() != null ? auction.getCreatedAt().toLocalDate() : null)
                .updatedAt(auction.getUpdatedAt() != null ? auction.getUpdatedAt().toLocalDate() : null)
                .build();
    }
}
