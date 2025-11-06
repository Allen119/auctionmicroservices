package com.onepiece.paymentservice.controller;

import com.onepiece.paymentservice.dto.PaymentRequestDTO;
import com.onepiece.paymentservice.dto.PaymentResponseDTO;
import com.onepiece.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/payment-service")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        log.info("Received payment request for auction ID: {}", paymentRequestDTO.getAuctionId());
        PaymentResponseDTO responseDTO = paymentService.createPayment(paymentRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentRequestDTO updatePaymentRequestDTO) {
        PaymentResponseDTO responseDTO = paymentService.updatePaymentStatus(id, updatePaymentRequestDTO);
        log.info("Updated payment: {}", responseDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/seller/{id}")
    public ResponseEntity<List<PaymentResponseDTO>> getBySellerId(@PathVariable Integer id) {
        List<PaymentResponseDTO> paymentDTOS = paymentService.getSellerPayments(id);
        return new ResponseEntity<>(paymentDTOS, HttpStatus.OK);
    }

    @GetMapping("/buyer/{id}")
    public ResponseEntity<List<PaymentResponseDTO>> getByBuyerId(@PathVariable Integer id) {
        List<PaymentResponseDTO> paymentDTOS = paymentService.getBuyerPayments(id);
        return new ResponseEntity<>(paymentDTOS, HttpStatus.OK);
    }

    @GetMapping("/seller/{id}/{status}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsBySellerStatus(
            @PathVariable Integer id,
            @PathVariable String status) {
        List<PaymentResponseDTO> paymentDTOS = paymentService.getSellerPaymentsByStatus(id, status);
        return new ResponseEntity<>(paymentDTOS, HttpStatus.OK);
    }

    @GetMapping("/buyer/{id}/{status}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByBuyerStatus(
            @PathVariable Integer id,
            @PathVariable String status) {
        List<PaymentResponseDTO> paymentDTOS = paymentService.getBuyerPaymentsByStatus(id, status);
        return new ResponseEntity<>(paymentDTOS, HttpStatus.OK);
    }
}