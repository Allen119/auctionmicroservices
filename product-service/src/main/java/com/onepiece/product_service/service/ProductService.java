package com.onepiece.product_service.service;

import com.onepiece.product_service.dto.ProductRequestDTO;
import com.onepiece.product_service.dto.ProductResponseDTO;
import com.onepiece.product_service.dto.ProductStatusUpdateDTO;
import com.onepiece.product_service.mapper.ProductMapper;
import com.onepiece.product_service.model.Product;
import com.onepiece.product_service.model.ProductImage;
import com.onepiece.product_service.repo.ProductImageRepo;
import com.onepiece.product_service.repo.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductImageRepo productImageRepo;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AuctionService auctionService;


    private static final long MAX_IMAGE_SIZE = 16 * 1024 * 1024;

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepo.findAll();
        return products.stream()
                .map(product -> {
                    List<ProductImage> images = productImageRepo.findByProductId(product.getProductId());
                    return productMapper.toResponseDTO(product, images);
                })
                .toList();
    }

    public ProductResponseDTO addProduct(ProductRequestDTO productDTO, MultipartFile mainImage,
                                         List<MultipartFile> additionalImages) throws IOException {

        log.info("addProduct called with sellerId: {}", productDTO.getSellerId());

        // Validate main image
        if (mainImage == null || mainImage.isEmpty()) {
            throw new IllegalArgumentException("Main image is required");
        }
        validateImageSize(mainImage, "Main image");

        // Validate additional images
        if (additionalImages != null) {
            for (MultipartFile additionalImage : additionalImages) {
                if (additionalImage != null && !additionalImage.isEmpty()) {
                    validateImageSize(additionalImage, "Additional image");
                }
            }
        }

        // Create and save product
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepo.save(product);
        log.info("Product saved with ID: {}", savedProduct.getProductId());

        // Save main image
        saveProductImage(savedProduct.getProductId(), mainImage);

        // Save additional images
        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (MultipartFile additionalImage : additionalImages) {
                if (additionalImage != null && !additionalImage.isEmpty()) {
                    saveProductImage(savedProduct.getProductId(), additionalImage);
                }
            }
        }

        List<ProductImage> allImages = productImageRepo.findByProductId(savedProduct.getProductId());
        return productMapper.toResponseDTO(savedProduct, allImages);
    }

    public ProductResponseDTO getProductById(int productId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            List<ProductImage> images = productImageRepo.findByProductId(productId);
            return productMapper.toResponseDTO(product, images);
        }
        throw new RuntimeException("Product not found with ID: " + productId);
    }

    public List<ProductResponseDTO> getProductsByCategory(Product.Category category) {
        List<Product> products = productRepo.getProductsByCategory(category);
        return products.stream()
                .map(product -> {
                    List<ProductImage> images = productImageRepo.findByProductId(product.getProductId());
                    return productMapper.toResponseDTO(product, images);
                })
                .toList();
    }

    public List<ProductResponseDTO> getProductsBySellerId(int sellerId) {
        if (!productRepo.existsBySellerId(sellerId)) {
            throw new RuntimeException("Seller not found with ID: " + sellerId);
        }
        List<Product> products = productRepo.getProductsBySellerId(sellerId);
        return products.stream()
                .map(product -> {
                    List<ProductImage> images = productImageRepo.findByProductId(product.getProductId());
                    return productMapper.toResponseDTO(product, images);
                })
                .toList();
    }

    @Transactional
    public ProductResponseDTO updateProduct(int productId, ProductRequestDTO productDTO,
                                            MultipartFile mainImage, List<MultipartFile> additionalImages) throws IOException {
        Optional<Product> existingProductOpt = productRepo.findById(productId);
        if (!existingProductOpt.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product existingProduct = existingProductOpt.get();
        productMapper.updateEntityFromDTO(productDTO, existingProduct);
        Product updatedProduct = productRepo.save(existingProduct);

        // Handle new images if provided
        if (mainImage != null && !mainImage.isEmpty()) {
            validateImageSize(mainImage, "Main image");
            saveProductImage(updatedProduct.getProductId(), mainImage);
        }

        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (MultipartFile additionalImage : additionalImages) {
                if (additionalImage != null && !additionalImage.isEmpty()) {
                    validateImageSize(additionalImage, "Additional image");
                    saveProductImage(updatedProduct.getProductId(), additionalImage);
                }
            }
        }

        List<ProductImage> allImages = productImageRepo.findByProductId(updatedProduct.getProductId());
        return productMapper.toResponseDTO(updatedProduct, allImages);
    }

    @Transactional
    public boolean deleteProduct(int productId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isPresent()) {
            productRepo.deleteById(productId);
            return true;
        }
        return false;
    }

    // Helper methods
    private void validateImageSize(MultipartFile image, String imageName) {
        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(imageName + " size exceeds 16MB limit. Current size: " +
                    (image.getSize() / (1024 * 1024)) + "MB");
        }
    }

    private void saveProductImage(Integer productId, MultipartFile imageFile) throws IOException {
        ProductImage productImage = new ProductImage();
        productImage.setProductId(productId);
        productImage.setImageData(imageFile.getBytes());
        productImageRepo.save(productImage);
    }
    @Transactional
    public ProductResponseDTO updateProductStatus(int productId, ProductStatusUpdateDTO statusUpdate) {
        log.info("========== UPDATE PRODUCT STATUS ==========");
        log.info("Searching for product ID: {}", productId);

        Optional<Product> productOpt = productRepo.findById(productId);

        if (!productOpt.isPresent()) {
            log.error("❌ Product NOT FOUND in database - ID: {}", productId);
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product product = productOpt.get();
        log.info("✓ Product found - ID: {}, Current Status: {}", product.getProductId(), product.getProductStatus());

        // Validate and update status
        try {
            Product.ProductStatus newStatus = Product.ProductStatus.valueOf(statusUpdate.getProductStatus().toUpperCase());
            product.setProductStatus(newStatus);
            log.info("✓ Status changed from {} to {}", product.getProductStatus(), newStatus);
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid status: {}", statusUpdate.getProductStatus());
            throw new IllegalArgumentException("Invalid product status. Allowed values: PENDING, APPROVED, DECLINED");
        }

        // ✅ SAVE TO DATABASE FIRST
        Product updatedProduct = productRepo.save(product);
        log.info("✅ Product SAVED to database - productId: {}, status: {}", updatedProduct.getProductId(), updatedProduct.getProductStatus());

        // ✅ FLUSH to ensure immediate persistence
        productRepo.flush();
        log.info("✅ Flushed to database");

        // ✅ RE-FETCH from database to verify
        Product verifyProduct = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Failed to verify saved product"));
        log.info("✅ Verified in database - ID: {}, Status: {}", verifyProduct.getProductId(), verifyProduct.getProductStatus());

        // ✅ Now call auction service AFTER successful save
        if (updatedProduct.getProductStatus() == Product.ProductStatus.APPROVED) {
            try {
                log.info("\n🎯 Product APPROVED - Creating Auction");
                log.info("   📦 Product ID: {}", updatedProduct.getProductId());

                auctionService.createAuctionForApprovedProduct(updatedProduct);

                log.info("✅ Auction creation initiated for product ID: {}", updatedProduct.getProductId());
            } catch (Exception e) {
                log.error("❌ Failed to create auction for product {}: {}", productId, e.getMessage());
                // Don't throw - auction failure shouldn't fail the product update
                log.warn("⚠️ Continuing despite auction error");
            }
        }

        List<ProductImage> images = productImageRepo.findByProductId(productId);
        return productMapper.toResponseDTO(verifyProduct, images);
    }
}