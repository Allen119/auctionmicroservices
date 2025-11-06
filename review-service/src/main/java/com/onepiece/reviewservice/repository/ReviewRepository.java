package com.onepiece.reviewservice.repository;

import com.onepiece.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Integer> {
    @Query("SELECT r FROM Review r WHERE r.sellerId = :sellerId AND r.createdBy = r.buyerId")
    List<Review> findBuyerReviewsForSeller(@Param("sellerId") Integer sellerId);

    @Query("SELECT r FROM Review r WHERE r.buyerId=:buyerId AND r.createdBy = r.sellerId")
    List<Review> findSellerReviewsForBuyer(@Param("buyerId") Integer buyerId);
}
