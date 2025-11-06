package com.onepiece.product_service.service;

import com.onepiece.product_service.dto.AuctionResponseDTO;
import com.onepiece.product_service.dto.BiddingResponseDTO;
import com.onepiece.product_service.dto.ProductRequestDTO;
import com.onepiece.product_service.dto.ProductResponseDTO;
import com.onepiece.product_service.mapper.ProductMapper;
import com.onepiece.product_service.model.Product;
import com.onepiece.product_service.model.ProductImage;
import com.onepiece.product_service.repo.ProductImageRepo;
import com.onepiece.product_service.repo.ProductRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductImageRepo productImageRepo;

    @Autowired
    private UserClientService userClientService;
    
    @Autowired
    private ProductMapper productMapper;


    @Autowired
    private AuctionClientService auctionClientService;

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepo.findAll();
        return products.stream()
                .map(product -> {
                    List<ProductImage> additionalImages = productImageRepo.findByProductId(product.getProductId());
                    return productMapper.toResponseDTO(product, additionalImages);
                })
                .toList();
    }
    
    public ProductResponseDTO addProduct(ProductRequestDTO productDTO, MultipartFile mainImage,
                                         List<MultipartFile> additionalImages) throws IOException {
        
        final long MAX_IMAGE_SIZE = 16 * 1024 * 1024;
        
        if (mainImage != null && !mainImage.isEmpty()) {
            if (mainImage.getSize() > MAX_IMAGE_SIZE) {
                throw new IllegalArgumentException("Main image size exceeds 16MB limit. Current size: " + 
                    (mainImage.getSize() / (1024 * 1024)) + "MB");
            }
        }
        
        if (additionalImages != null) {
            for (MultipartFile additionalImage : additionalImages) {
                if (additionalImage != null && !additionalImage.isEmpty()) {
                    if (additionalImage.getSize() > MAX_IMAGE_SIZE) {
                        throw new IllegalArgumentException("Additional image size exceeds 16MB limit. Current size: " + 
                            (additionalImage.getSize() / (1024 * 1024)) + "MB");
                    }
                }
            }
        }
        
        Product product = productMapper.toEntity(productDTO);
        
        product.setCreatedAt(LocalDate.now());
        product.setUpdatedAt(LocalDate.now());
        product.setCreatedBy(productDTO.getSellerId());
        product.setUpdatedBy(productDTO.getSellerId());
        
        if (mainImage != null && !mainImage.isEmpty()) {
            product.setProductImg(mainImage.getBytes());
        }
        
        Product savedProduct = productRepo.save(product);
        
        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (MultipartFile additionalImage : additionalImages) {
                if (additionalImage != null && !additionalImage.isEmpty()) {
                    ProductImage productImage = new ProductImage();
                    productImage.setProductId(savedProduct.getProductId());
                    productImage.setImageData(additionalImage.getBytes());
                    productImageRepo.save(productImage);
                }
            }
        }
        
        List<ProductImage> additionalImagesFromDB = productImageRepo.findByProductId(savedProduct.getProductId());
        
        return productMapper.toResponseDTO(savedProduct, additionalImagesFromDB);
    }

    public ProductResponseDTO getProductById(int productId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            List<ProductImage> additionalImages = productImageRepo.findByProductId(productId);
            return productMapper.toResponseDTO(product, additionalImages);
        }
        throw new RuntimeException("Product not found with ID: " + productId);
    }

    public List<ProductResponseDTO> getProductsByCategory(Product.Category category) {
        List<Product> products = productRepo.getProductsByCategory(category);
        return products.stream()
                .map(product -> {
                    List<ProductImage> additionalImages = productImageRepo.findByProductId(product.getProductId());
                    return productMapper.toResponseDTO(product, additionalImages);
                })
                .toList();
    }

    public List<ProductResponseDTO> getProductsBySellerId(int sellerId) {
        boolean seller_check = userClientService.checkSellerRole(sellerId);
        if(seller_check){
            List<Product> products = productRepo.getProductsBySellerId(sellerId);
            return products.stream()
                    .map(product -> {
                        List<ProductImage> additionalImages = productImageRepo.findByProductId(product.getProductId());
                        return productMapper.toResponseDTO(product, additionalImages);
                    })
                    .toList();
        } else {
            throw new ProviderNotFoundException("Seller not Found");
        }
    }

    @Transactional
    public ProductResponseDTO updateProduct(int productId, @Valid ProductRequestDTO productRequest, MultipartFile mainImage, List<MultipartFile> additionalImages) throws IOException {
        Optional<Product> existingProductOpt = productRepo.findById(productId);

        if (!existingProductOpt.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product existingProduct = existingProductOpt.get();

        // Update product fields from DTO
        productMapper.updateEntityFromDTO(productRequest, existingProduct);

        // Update main image if provided
        if (mainImage != null && !mainImage.isEmpty()) {
            final long MAX_IMAGE_SIZE = 16 * 1024 * 1024;
            if (mainImage.getSize() > MAX_IMAGE_SIZE) {
                throw new IllegalArgumentException("Main image size exceeds 16MB limit. Current size: " +
                    (mainImage.getSize() / (1024 * 1024)) + "MB");
            }
            existingProduct.setProductImg(mainImage.getBytes());
        }

        // Update metadata
        existingProduct.setUpdatedAt(LocalDate.now());
        existingProduct.setUpdatedBy(productRequest.getSellerId());

        // Save updated product
        Product updatedProduct = productRepo.save(existingProduct);

        // Handle additional images if provided
        if (additionalImages != null && !additionalImages.isEmpty()) {
            final long MAX_IMAGE_SIZE = 16 * 1024 * 1024;
            for (MultipartFile additionalImage : additionalImages) {
                if (additionalImage != null && !additionalImage.isEmpty()) {
                    if (additionalImage.getSize() > MAX_IMAGE_SIZE) {
                        throw new IllegalArgumentException("Additional image size exceeds 16MB limit. Current size: " +
                            (additionalImage.getSize() / (1024 * 1024)) + "MB");
                    }
                    ProductImage productImage = new ProductImage();
                    productImage.setProductId(updatedProduct.getProductId());
                    productImage.setImageData(additionalImage.getBytes());
                    productImageRepo.save(productImage);
                }
            }
        }

        // Get all additional images and return response
        List<ProductImage> additionalImagesFromDB = productImageRepo.findByProductId(updatedProduct.getProductId());
        return productMapper.toResponseDTO(updatedProduct, additionalImagesFromDB);
    }

    @Transactional
    public boolean deleteProduct(int productId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (productOpt.isPresent()) {
            List<AuctionResponseDTO> auctions = auctionClientService.getAuctionsByProductId(productId);

            for (AuctionResponseDTO auction : auctions) {
                List<BiddingResponseDTO> biddings = auctionClientService.getBidsByAuctionId(auction.getAuctionId());
                if (!biddings.isEmpty()) {
                    for(BiddingResponseDTO bidding : biddings)
                    auctionClientService.deleteBidById(bidding.getBidId());
                }
                auctionClientService.deleteAuction(auction.getAuctionId());

            }

            productImageRepo.deleteByProductId(productId);
            
            productRepo.deleteById(productId);
            return true;
        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    public List<ProductResponseDTO> getPendingProducts() {
        List<Product> products = productRepo.getPendingProducts();
        return products.stream()
                .map(product -> {
                    List<ProductImage> additionalImages = productImageRepo.findByProductId(product.getProductId());
                    return productMapper.toResponseDTO(product, additionalImages);
                })
                .toList();
    }

    public List<ProductResponseDTO> getProductsByStatus(Product.ProductStatus status) {
        List<Product> products = productRepo.getProductsByStatus(status);
        return products.stream()
                .map(product -> {
                    List<ProductImage> additionalImages = productImageRepo.findByProductId(product.getProductId());
                    return productMapper.toResponseDTO(product, additionalImages);
                })
                .toList();
    }

    @Transactional
    public ProductResponseDTO approveProduct(int productId, int adminId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product product = productOpt.get();
        product.setProductStatus(Product.ProductStatus.APPROVED);
        product.setUpdatedAt(LocalDate.now());
        product.setUpdatedBy(adminId);

        Product updatedProduct = productRepo.save(product);
        List<ProductImage> additionalImages = productImageRepo.findByProductId(productId);
        return productMapper.toResponseDTO(updatedProduct, additionalImages);
    }

    @Transactional
    public ProductResponseDTO declineProduct(int productId, int adminId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product product = productOpt.get();
        product.setProductStatus(Product.ProductStatus.DECLINED);
        product.setUpdatedAt(LocalDate.now());
        product.setUpdatedBy(adminId);

        Product updatedProduct = productRepo.save(product);
        List<ProductImage> additionalImages = productImageRepo.findByProductId(productId);
        return productMapper.toResponseDTO(updatedProduct, additionalImages);
    }

    @Transactional
    public ProductResponseDTO updateProductStatus(int productId, Product.ProductStatus status, int adminId) {
        Optional<Product> productOpt = productRepo.findById(productId);
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product product = productOpt.get();
        product.setProductStatus(status);
        product.setUpdatedAt(LocalDate.now());
        product.setUpdatedBy(adminId);

        Product updatedProduct = productRepo.save(product);
        List<ProductImage> additionalImages = productImageRepo.findByProductId(productId);
        return productMapper.toResponseDTO(updatedProduct, additionalImages);
    }
}
