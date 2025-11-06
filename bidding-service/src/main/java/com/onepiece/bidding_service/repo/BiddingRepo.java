package com.onepiece.bidding_service.repo;

import com.onepiece.bidding_service.model.Bidding;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BiddingRepo extends JpaRepository<Bidding, Integer> {

    List<Bidding> findByAuctionId(int auctionId);

    List<Bidding> findByBuyerId(int buyerId);

    @Query("SELECT b FROM Bidding b WHERE b.auctionId = :auctionId ORDER BY b.newBidAmount DESC")
    List<Bidding> findByAuctionIdOrderByBidAmountDesc(int auctionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bidding b WHERE b.bidId = :bidId")
    Optional<Bidding> findByIdWithLock(int bidId);
}
