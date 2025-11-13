package com.onepiece.product_service.service;

import com.onepiece.product_service.dto.AuctionRequestDTO;
import com.onepiece.product_service.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class AuctionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${payment.service.url}")
    private String biddingServiceUrl;

    @Value("${service.secret:product-service-secret-key-12345}")
    private String serviceSecret;

    public void createAuctionForApprovedProduct(Product product) {
        log.info("\n════════════════════════════════════════════════════════════");
        log.info("🎯 PRODUCT APPROVED - Initiating ISC with Bidding Service");
        log.info("Product ID: {}", product.getProductId());
        log.info("Bidding Service URL: {}", biddingServiceUrl);
        log.info("════════════════════════════════════════════════════════════");

        try {
            // ========== STEP 1: Get Product Details ==========
            log.info("\n[STEP 1️⃣] Fetching Product Details...");
            log.info("✓ Product Found:");
            log.info("  📦 Product ID: {}", product.getProductId());
            log.info("  📝 Model: {}", product.getProductModel());
            log.info("  💰 Start Price: ₹{}", product.getStartPrice());
            log.info("  🏷️  Price Jump: ₹{}", product.getPriceJump());
            log.info("  👤 Seller ID: {}", product.getSellerId());

            // ========== STEP 2: Create Auction Request ==========
            log.info("\n[STEP 2️⃣] Creating Auction Request DTO...");
            AuctionRequestDTO auctionDTO = buildAuctionRequestDTO(product);

            log.info("✓ Auction Request DTO Built:");
            log.info("  📦 Product ID: {}", auctionDTO.getProductId());
            log.info("  💵 Starting Price: ₹{}", auctionDTO.getStartingPrice());  // ✅ FIXED
            log.info("  🏷️  Price Jump: ₹{}", auctionDTO.getPriceJump());
            log.info("  📊 Bid Count: {}", auctionDTO.getBidCount());
            log.info("  🔔 Status: {}", auctionDTO.getCurrStatus());
            log.info("  👤 Seller ID: {}", auctionDTO.getSellerId());

            // Log the JSON that will be sent
            try {
                String jsonPayload = objectMapper.writeValueAsString(auctionDTO);
                log.info("  📤 JSON Payload: {}", jsonPayload);
            } catch (Exception e) {
                log.warn("Could not serialize DTO to JSON: {}", e.getMessage());
            }

            // ========== STEP 3: Call Bidding Service ==========
            log.info("\n[STEP 3️⃣] Calling Bidding Service...");
            callBiddingService(auctionDTO);

            log.info("\n✅ SUCCESS! Auction Created in Bidding Service");
            log.info("════════════════════════════════════════════════════════════\n");

        } catch (Exception e) {
            log.error("❌ Error in Product Approval ISC: {}", e.getMessage());
            log.error("Exception Details: ", e);
            throw new RuntimeException("Auction creation failed: " + e.getMessage());
        }
    }

    private AuctionRequestDTO buildAuctionRequestDTO(Product product) {
        log.debug("Building AuctionRequestDTO from Product: {}", product.getProductId());

        AuctionRequestDTO auctionDTO = new AuctionRequestDTO();
        auctionDTO.setProductId(product.getProductId());
        auctionDTO.setStartingPrice(product.getStartPrice());  // ✅ startingPrice (not currPrice)
        auctionDTO.setPriceJump(product.getPriceJump());
        auctionDTO.setBidCount(0);
        auctionDTO.setCurrStatus("SCHEDULED");
        auctionDTO.setSellerId(product.getSellerId());

        log.debug("AuctionRequestDTO built successfully");
        return auctionDTO;
    }

    private void callBiddingService(AuctionRequestDTO auctionDTO) {
        try {
            log.info("\n[STEP 4️⃣] Preparing ISC Request...");

            String url = biddingServiceUrl + "/auctions/create-auction";

            log.info("🔄 Target URL: {}", url);
            log.info("🔄 HTTP Method: POST");

            // ========== Create Headers ==========
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Add service authentication header
            headers.add("X-Service-Secret", serviceSecret);
            log.info("📡 Header: X-Service-Secret = [PROTECTED]");

            // Forward JWT headers from incoming request
            HttpServletRequest httpRequest = getHttpServletRequest();
            if (httpRequest != null) {
                String userId = httpRequest.getHeader("X-Auth-User-Id");
                String userName = httpRequest.getHeader("X-Auth-User-Name");
                String userRoles = httpRequest.getHeader("X-Auth-User-Roles");

                if (userId != null && !userId.isEmpty()) {
                    headers.add("X-Auth-User-Id", userId);
                    log.info("📡 Header: X-Auth-User-Id = {}", userId);
                }
                if (userName != null && !userName.isEmpty()) {
                    headers.add("X-Auth-User-Name", userName);
                    log.info("📡 Header: X-Auth-User-Name = {}", userName);
                }
                if (userRoles != null && !userRoles.isEmpty()) {
                    headers.add("X-Auth-User-Roles", userRoles);
                    log.info("📡 Header: X-Auth-User-Roles = {}", userRoles);
                }
            } else {
                log.warn("⚠️ Could not get HTTP request context - using fallback");
                headers.add("X-Auth-User-Id", "1");
                headers.add("X-Auth-User-Name", "Allen119");
                headers.add("X-Auth-User-Roles", "ROLE_ADMIN");
                log.info("📡 Header: X-Auth-User-Id = 1 (fallback)");
                log.info("📡 Header: X-Auth-User-Name = Allen119 (fallback)");
                log.info("📡 Header: X-Auth-User-Roles = ROLE_ADMIN (fallback)");
            }

            log.info("📋 All Headers:");
            headers.forEach((key, values) -> {
                if (!key.equals("X-Service-Secret")) {
                    log.info("   {} = {}", key, values.get(0));
                }
            });

            // ========== Create and Send Request ==========
            log.info("\n[STEP 5️⃣] Sending ISC Request to Bidding Service...");
            log.info("⏱️  Request Timestamp: {}", System.currentTimeMillis());

            HttpEntity<AuctionRequestDTO> request = new HttpEntity<>(auctionDTO, headers);

            log.info("🚀 Making HTTP POST request to: {}", url);
            Object response = restTemplate.postForObject(url, request, Object.class);

            log.info("\n✓ Bidding Service Response Received!");
            log.info("  Status: 201 CREATED");
            log.info("  Response Body: {}", response);
            log.info("════════════════════════════════════════════════════════════");

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("❌ HTTP Error Response from Bidding Service");
            log.error("  Status Code: {}", e.getStatusCode());
            log.error("  Status Text: {}", e.getStatusText());
            log.error("  Response Body: {}", e.getResponseBodyAsString());
            log.error("  Exception Details: ", e);
            throw new RuntimeException("Failed to create auction in bidding service: " + e.getMessage());
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("❌ Server Error from Bidding Service (5xx)");
            log.error("  Status Code: {}", e.getStatusCode());
            log.error("  Response Body: {}", e.getResponseBodyAsString());
            log.error("  Exception Details: ", e);
            throw new RuntimeException("Bidding service error: " + e.getMessage());
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("❌ Connection Error to Bidding Service");
            log.error("  Could not connect to: {}", biddingServiceUrl);
            log.error("  Error: {}", e.getMessage());
            log.error("  Exception Details: ", e);
            throw new RuntimeException("Could not reach bidding service: " + e.getMessage());
        } catch (Exception e) {
            log.error("❌ Bidding Service Call Failed");
            log.error("  Exception Type: {}", e.getClass().getName());
            log.error("  Error Message: {}", e.getMessage());
            log.error("  Exception Details: ", e);
            throw new RuntimeException("Failed to create auction in bidding service: " + e.getMessage());
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                log.debug("✓ HTTP Request Context Found");
                return attributes.getRequest();
            }
            log.debug("⚠️ HTTP Request Context is NULL");
        } catch (Exception e) {
            log.debug("Could not get HTTP request context: {}", e.getMessage());
        }
        return null;
    }
}