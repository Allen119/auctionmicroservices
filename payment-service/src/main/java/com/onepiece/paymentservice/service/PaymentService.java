package com.onepiece.paymentservice.service;

import com.onepiece.paymentservice.dto.PaymentRequestDTO;
import com.onepiece.paymentservice.dto.PaymentResponseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createPayment(@Valid PaymentRequestDTO paymentRequestDTO);

    PaymentResponseDTO updatePaymentStatus(Integer id, PaymentRequestDTO updatePaymentRequestDTO);

    List<PaymentResponseDTO> getBuyerPayments(Integer id);

    List<PaymentResponseDTO> getSellerPayments(Integer id);

    List<PaymentResponseDTO> getSellerPaymentsByStatus(Integer id, String status);

    List<PaymentResponseDTO> getBuyerPaymentsByStatus(Integer id, String status);
}
