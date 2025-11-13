package com.onepiece.bidding_service.repo;

import com.onepiece.bidding_service.model.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BiddingRepo extends JpaRepository<Bidding, Integer> {

    List<Bidding> findByAuctionIdOrderByNewBidAmountDesc(int auctionId);

    List<Bidding> findByBuyerId(int buyerId);

    @Query("SELECT b FROM Bidding b WHERE b.auctionId = :auctionId ORDER BY b.newBidAmount DESC LIMIT 1")
    Bidding findHighestBidForAuction(int auctionId);

    long countByAuctionId(int auctionId);

    @Query("SELECT b FROM Bidding b WHERE b.auctionId = :auctionId ORDER BY b.newBidAmount DESC, b.bidTime DESC")
    List<Bidding> findWinnerByAuctionId(@Param("auctionId") Integer auctionId);
    //Explicit mapping
}