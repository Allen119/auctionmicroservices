package com.onepiece.product_service.mapper;

import com.onepiece.product_service.dto.ProductRequestDTO;
import com.onepiece.product_service.dto.ProductResponseDTO;
import com.onepiece.product_service.model.Product;
import com.onepiece.product_service.model.ProductImage;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setSellerId(dto.getSellerId());
        product.setProductModel(dto.getProductModel());
        product.setModelYear(dto.getModelYear());
        product.setStartPrice(dto.getStartPrice());
        product.setPriceJump(dto.getPriceJump());
        product.setDescription(dto.getDescription());
        product.setAuctionDate(dto.getAuctionDate());
        product.setAuctionStartTime(dto.getAuctionStartTime());
        product.setAuctionDuration(dto.getAuctionDuration());
        product.setCategory(getCategory(dto.getCategory()));
        product.setProductStatus(Product.ProductStatus.PENDING);
        return product;
    }

    public ProductResponseDTO toResponseDTO(Product product, List<ProductImage> images) {
        List<String> imageUrls = null;
        if (images != null && !images.isEmpty()) {
            imageUrls = images.stream()
                    .map(img -> Base64.getEncoder().encodeToString(img.getImageData()))
                    .toList();
        }

        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .sellerId(product.getSellerId())
                .productModel(product.getProductModel())
                .modelYear(product.getModelYear())
                .startPrice(product.getStartPrice())
                .priceJump(product.getPriceJump())
                .description(product.getDescription())
                .auctionDate(product.getAuctionDate())
                .auctionStartTime(product.getAuctionStartTime())
                .auctionDuration(product.getAuctionDuration())
                .category(product.getCategory().toString())
                // ✅ Add productStatus
                .productStatus(product.getProductStatus() != null ? product.getProductStatus().toString() : null)
                .imageUrls(imageUrls)
                .build();
    }

    public void updateEntityFromDTO(ProductRequestDTO dto, Product product) {
        if (dto.getProductModel() != null) product.setProductModel(dto.getProductModel());
        if (dto.getModelYear() != null) product.setModelYear(dto.getModelYear());
        if (dto.getStartPrice() != null) product.setStartPrice(dto.getStartPrice());
        if (dto.getPriceJump() != null) product.setPriceJump(dto.getPriceJump());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getAuctionDate() != null) product.setAuctionDate(dto.getAuctionDate());
        if (dto.getAuctionStartTime() != null) product.setAuctionStartTime(dto.getAuctionStartTime());
        if (dto.getAuctionDuration() != null) product.setAuctionDuration(dto.getAuctionDuration());
        if (dto.getCategory() != null) product.setCategory(getCategory(dto.getCategory()));
    }

    private Product.Category getCategory(String category) {
        if (category == null) throw new IllegalArgumentException("Category cannot be null");

        return switch (category.toLowerCase()) {
            case "antique" -> Product.Category.Antique;
            case "vintage" -> Product.Category.Vintage;
            case "classic" -> Product.Category.Classic;
            case "sports" -> Product.Category.Sports;
            case "luxury" -> Product.Category.Luxury;
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        };
    }
}