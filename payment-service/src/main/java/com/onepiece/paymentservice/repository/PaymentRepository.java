package com.onepiece.paymentservice.repository;

import com.onepiece.paymentservice.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payments, Integer> {
    @Query("SELECT p FROM Payments p WHERE p.sellerId = :sellerId AND p.createdBy = p.sellerId")
    List<Payments> findBuyerPayments(Integer sellerId);
    @Query("SELECT p FROM Payments p WHERE p.buyerId = :buyerId AND p.createdBy = p.buyerId")
    List<Payments> findSellerPayments(Integer buyerId);

    @Query("SELECT p FROM Payments p WHERE p.sellerId = :sellerId AND p.createdBy = p.sellerId AND p.transactionStatus = :status")
    List<Payments> findSellerPaymentsByStatus(@Param("sellerId") Integer sellerId, @Param("status") String status);

    @Query("SELECT p FROM Payments p WHERE p.buyerId = :buyerId AND p.createdBy = p.buyerId AND p.transactionStatus = :status")
    List<Payments> findBuyerPaymentsByStatus(@Param("buyerId") Integer buyerId, @Param("status") String status);
}
