package com.onepiece.bidding_service.repo;

import com.onepiece.bidding_service.model.Auction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepo extends JpaRepository<Auction, Integer> {

    // Find auctions by product ID
    List<Auction> findByProductId(int productId);

    // Find auction by status
    List<Auction> findByCurrStatus(Auction.currStatus currStatus);

    // Find auction by ID with pessimistic lock (for concurrent bidding)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.auctionId = :auctionId")
    Optional<Auction> findByIdWithLock(@Param("auctionId") int auctionId);  // ✅ FIXED: Added @Param
}