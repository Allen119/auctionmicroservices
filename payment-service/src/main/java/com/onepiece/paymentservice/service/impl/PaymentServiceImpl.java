package com.onepiece.paymentservice.service.impl;

import com.onepiece.paymentservice.dto.PaymentRequestDTO;
import com.onepiece.paymentservice.dto.PaymentResponseDTO;
import com.onepiece.paymentservice.model.Payments;
import com.onepiece.paymentservice.repository.PaymentRepository;
import com.onepiece.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    public final PaymentRepository paymentRepository;
    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) {
        Payments paymentEntity = new Payments(
                paymentRequestDTO.getBuyerId(),
                paymentRequestDTO.getSellerId(),
                paymentRequestDTO.getTransactionId(),
                paymentRequestDTO.getProductId(),
                paymentRequestDTO.getAuctionId(),
                paymentRequestDTO.getFinalAmount(),
                paymentRequestDTO.getPaymentMethod(),
                paymentRequestDTO.getTransactionStatus(),
                paymentRequestDTO.getCreatedBy(),
                paymentRequestDTO.getUpdatedBy()
        );

        Payments payObj = paymentRepository.save(paymentEntity);
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(
                payObj.getId(),
                payObj.getBuyerId(),
                payObj.getSellerId(),
                payObj.getTransactionId(),
                payObj.getProductId(),
                payObj.getAuctionId(),
                payObj.getFinalAmount(),
                payObj.getPaymentMethod(),
                payObj.getTransactionStatus(),
                payObj.getPaymentTime(),
                payObj.getCreatedAt(),
                payObj.getUpdatedAt(),
                payObj.getCreatedBy(),
                payObj.getUpdatedBy()
        );
        return responseDTO;
    }

    @Override
    public PaymentResponseDTO updatePaymentStatus(Integer id, PaymentRequestDTO updatePaymentRequestDTO) {
        Optional<Payments> optionalPayments = paymentRepository.findById(id);
        if(optionalPayments.isEmpty()){
            throw new RuntimeException("Payment not found with id: " + id);
        }

        Payments existingPayments = optionalPayments.get();
        existingPayments.updateStatusDetails(
                updatePaymentRequestDTO.getTransactionId(),
                updatePaymentRequestDTO.getPaymentMethod(),
                updatePaymentRequestDTO.getFinalAmount(),
                updatePaymentRequestDTO.getTransactionStatus(),
                updatePaymentRequestDTO.getUpdatedBy()
        );

        Payments savedPayments = paymentRepository.save(existingPayments);
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(
                savedPayments.getId(),
                savedPayments.getBuyerId(),
                savedPayments.getSellerId(),
                savedPayments.getTransactionId(),
                savedPayments.getProductId(),
                savedPayments.getAuctionId(),
                savedPayments.getFinalAmount(),
                savedPayments.getPaymentMethod(),
                savedPayments.getTransactionStatus(),
                savedPayments.getPaymentTime(),
                savedPayments.getCreatedAt(),
                savedPayments.getUpdatedAt(),
                savedPayments.getCreatedBy(),
                savedPayments.getUpdatedBy()
        );
        return responseDTO;
    }

    @Override
    public List<PaymentResponseDTO> getBuyerPayments(Integer SellerId) {
        List<Payments> paymentsList = paymentRepository.findBuyerPayments(SellerId);
        List<PaymentResponseDTO> paymentDTOList = paymentsList.stream()
                .map(e-> new PaymentResponseDTO(e.getId(),
                e.getBuyerId(), e.getSellerId(), e.getTransactionId(),
                e.getProductId(), e.getAuctionId(), e.getFinalAmount(),
                e.getPaymentMethod(), e.getTransactionStatus(), e.getPaymentTime(), e.getCreatedAt(), e.getUpdatedAt(),e.getCreatedBy(),e.getUpdatedBy()))
                .collect(Collectors.toList());
        return  paymentDTOList;
    }

    @Override
    public List<PaymentResponseDTO> getSellerPayments(Integer buyerId) {
        List<Payments> paymentsList = paymentRepository.findSellerPayments(buyerId);
        List<PaymentResponseDTO> paymentDTOList = paymentsList.stream()
                .map(e-> new PaymentResponseDTO(e.getId(),
                        e.getBuyerId(), e.getSellerId(), e.getTransactionId(),
                        e.getProductId(), e.getAuctionId(), e.getFinalAmount(),
                        e.getPaymentMethod(), e.getTransactionStatus(), e.getPaymentTime(),e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(),e.getUpdatedBy()))
                .collect(Collectors.toList());
        return  paymentDTOList;
    }

    @Override
    public List<PaymentResponseDTO> getSellerPaymentsByStatus(Integer sellerId, String status) {
        List<Payments> paymentsList = paymentRepository.findSellerPaymentsByStatus(sellerId, status);
        return paymentsList.stream()
                .map(e -> new PaymentResponseDTO(
                        e.getId(),
                        e.getBuyerId(),
                        e.getSellerId(),
                        e.getTransactionId(),
                        e.getProductId(),
                        e.getAuctionId(),
                        e.getFinalAmount(),
                        e.getPaymentMethod(),
                        e.getTransactionStatus(),
                        e.getPaymentTime(),
                        e.getCreatedAt(),
                        e.getUpdatedAt(),
                        e.getCreatedBy(),
                        e.getUpdatedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponseDTO> getBuyerPaymentsByStatus(Integer buyerId, String status) {
        List<Payments> paymentsList = paymentRepository.findBuyerPaymentsByStatus(buyerId, status);
        return paymentsList.stream()
                .map(e -> new PaymentResponseDTO(
                        e.getId(),
                        e.getBuyerId(),
                        e.getSellerId(),
                        e.getTransactionId(),
                        e.getProductId(),
                        e.getAuctionId(),
                        e.getFinalAmount(),
                        e.getPaymentMethod(),
                        e.getTransactionStatus(),
                        e.getPaymentTime(),
                        e.getCreatedAt(),
                        e.getUpdatedAt(),
                        e.getCreatedBy(),
                        e.getUpdatedBy()))
                .collect(Collectors.toList());
    }

}