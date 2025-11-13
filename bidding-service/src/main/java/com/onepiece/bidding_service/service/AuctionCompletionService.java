package com.onepiece.bidding_service.service;

import com.onepiece.bidding_service.dto.CreatePaymentRequestDTO;
import com.onepiece.bidding_service.model.Auction;
import com.onepiece.bidding_service.model.Bidding;
import com.onepiece.bidding_service.repo.AuctionRepo;
import com.onepiece.bidding_service.repo.BiddingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * ✅ ISC: Auction → Payment Service Communication
 * Called when auction status changes to COMPLETED
 *
 * Forwards JWT headers from incoming request to Payment Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionCompletionService {

    private final AuctionRepo auctionRepo;
    private final BiddingRepo biddingRepo;
    private final RestTemplate restTemplate;

    @Value("${payment.service.url:http://localhost:4040/api/v1/payment-service}")
    private String paymentServiceUrl;

    @Value("${service.secret:bidding-service-secret-key-12345}")
    private String serviceSecret;

    /**
     * ✅ When Auction Status Changes to COMPLETED
     * Find winner and call Payment Service via ISC
     * Forwards JWT headers from original request
     */
    @Transactional
    public void handleAuctionCompletion(Integer auctionId) {

        log.info("\n════════════════════════════════════════════════════════════");
        log.info("🎯 AUCTION COMPLETED - Initiating ISC with Payment Service");
        log.info("Auction ID: {}", auctionId);
        log.info("════════════════════════════════════════════════════════════");

        try {
            // ========== STEP 1: Get Auction ==========
            log.info("\n[STEP 1️⃣] Fetching Auction Details...");
            Auction auction = auctionRepo.findById(auctionId)
                    .orElseThrow(() -> new RuntimeException("❌ Auction not found: " + auctionId));

            log.info("✓ Auction Found:");
            log.info("  📦 Product ID: {}", auction.getProductId());
            log.info("  👤 Seller ID: {}", auction.getCreatedBy());
            log.info("  💰 Current Price: ₹{}", auction.getCurrPrice());
            log.info("  🏷️  Bid Count: {}", auction.getBidCount());

            // ========== STEP 2: Find Winner (Highest Bid) ==========
            log.info("\n[STEP 2️⃣] Finding Winner (Highest Bidder)...");

            List<Bidding> bids = biddingRepo.findWinnerByAuctionId(auctionId);

            if (bids.isEmpty()) {
                log.warn("⚠️ No bids found - Auction completed with no bidder");
                return;
            }

            Bidding winningBid = bids.get(0);
            Integer buyerId = winningBid.getBuyerId();
            Integer finalAmount = winningBid.getNewBidAmount();
            Integer sellerId = auction.getCreatedBy();
            Integer productId = auction.getProductId();

            log.info("✓ Winner Found:");
            log.info("  🏆 Buyer ID: {}", buyerId);
            log.info("  💵 Winning Amount: ₹{}", finalAmount);

            // ========== STEP 3: Create Payment Request ==========
            log.info("\n[STEP 3️⃣] Creating Payment Request...");
            CreatePaymentRequestDTO paymentRequest = new CreatePaymentRequestDTO(
                    buyerId,
                    sellerId,
                    productId,
                    auctionId,
                    finalAmount
            );

            log.info("✓ Payment Request:");
            log.info("  Buyer: {}", buyerId);
            log.info("  Seller: {}", sellerId);
            log.info("  Product: {}", productId);
            log.info("  Amount: ₹{}", finalAmount);

            // ========== STEP 4: Call Payment Service ==========
            log.info("\n[STEP 4️⃣] Calling Payment Service...");
            log.info("🔄 POST to: {}", paymentServiceUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // ✅ Add service authentication header
            headers.add("X-Service-Secret", serviceSecret);
            log.info("📡 Adding X-Service-Secret header for service authentication");

            // ✅ Forward JWT headers from incoming request to Payment Service
            HttpServletRequest httpRequest = getHttpServletRequest();
            if (httpRequest != null) {
                String userId = httpRequest.getHeader("X-Auth-User-Id");
                String userName = httpRequest.getHeader("X-Auth-User-Name");
                String userRoles = httpRequest.getHeader("X-Auth-User-Roles");

                // Forward JWT headers if present
                if (userId != null && !userId.isEmpty()) {
                    headers.add("X-Auth-User-Id", userId);
                    log.info("📤 Forwarding X-Auth-User-Id: {}", userId);
                }
                if (userName != null && !userName.isEmpty()) {
                    headers.add("X-Auth-User-Name", userName);
                    log.info("📤 Forwarding X-Auth-User-Name: {}", userName);
                }
                if (userRoles != null && !userRoles.isEmpty()) {
                    headers.add("X-Auth-User-Roles", userRoles);
                    log.info("📤 Forwarding X-Auth-User-Roles: {}", userRoles);
                }
            } else {
                log.warn("⚠️ Could not get HTTP request context (running in async context)");
            }

            HttpEntity<CreatePaymentRequestDTO> request = new HttpEntity<>(paymentRequest, headers);

            // ========== STEP 5: SEND REQUEST TO PAYMENT SERVICE ==========
            // ✅ THIS IS MISSING IN YOUR CODE - ADD THIS PART
            try {
                log.info("\n[STEP 5️⃣] Sending ISC Request to Payment Service...");

                Object response = restTemplate.postForObject(
                        paymentServiceUrl,
                        request,
                        Object.class
                );

                log.info("\n✅ SUCCESS! Payment Created in Payment Service");
                log.info("════════════════════════════════════════════════════════════");
                log.info("Response: {}", response);
                log.info("════════════════════════════════════════════════════════════\n");

            } catch (Exception e) {
                log.error("❌ Payment Service Call Failed: {}", e.getMessage());
                log.error("Exception Details: ", e);
                // Don't throw - ISC failure shouldn't break auction completion
            }

        } catch (Exception e) {
            log.error("❌ Error in Auction Completion ISC: {}", e.getMessage());
            log.error("Exception Details: ", e);
        }
    }

    /**
     * ✅ Helper method to get current HTTP request
     * Returns null if not in HTTP request context (e.g., in async execution)
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            log.debug("Could not get HTTP request context: {}", e.getMessage());
        }
        return null;
    }
}