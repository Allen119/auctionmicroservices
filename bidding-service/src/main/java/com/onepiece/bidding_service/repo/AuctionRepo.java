package com.onepiece.bidding_service.repo;

import com.onepiece.bidding_service.model.Auction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepo extends JpaRepository<Auction, Integer> {

    List<Auction> findByProductId(int productId);
    
    List<Auction> findByCurrStatus(Auction.CurrStatus currStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.auctionId = :auctionId")
    Optional<Auction> findByIdWithLock(int auctionId);
}
