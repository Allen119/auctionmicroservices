package com.onepiece.product_service.mapper;


import com.onepiece.product_service.dto.ProductRequestDTO;
import com.onepiece.product_service.dto.ProductResponseDTO;
import com.onepiece.product_service.model.Product;
import com.onepiece.product_service.model.ProductImage;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
        
        try {
            String categoryInput = dto.getCategory().toLowerCase();
            Product.Category categoryEnum;
            
            switch (categoryInput) {
                case "antique":
                    categoryEnum = Product.Category.Antique;
                    break;
                case "vintage":
                    categoryEnum = Product.Category.Vintage;
                    break;
                case "classic":
                    categoryEnum = Product.Category.Classic;
                    break;
                case "sports":
                    categoryEnum = Product.Category.Sports;
                    break;
                case "luxury":
                    categoryEnum = Product.Category.Luxury;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid category: " + dto.getCategory() + 
                        ". Valid categories are: Antique, Vintage, Classic, Sports, Luxury");
            }
            
            product.setCategory(categoryEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + dto.getCategory() + 
                ". Valid categories are: Antique, Vintage, Classic, Sports, Luxury");
        }
        
        return product;
    }

    public ProductResponseDTO toResponseDTO(Product product, List<ProductImage> additionalImages) {
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
                .mainImageUrl(product.getProductImg() != null ?
                    Base64.getEncoder().encodeToString(product.getProductImg()) : null)
                .additionalImageUrls(additionalImages != null ? 
                    additionalImages.stream()
                            .map(img -> img.getImageData() != null ? 
                                Base64.getEncoder().encodeToString(img.getImageData()) : null)
                            .collect(Collectors.toList()) : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductResponseDTO toResponseDTO(Product product) {
        return toResponseDTO(product, null);
    }

    public void updateEntityFromDTO(@Valid ProductRequestDTO productRequest, Product existingProduct) {
        if (productRequest.getProductModel() != null) {
            existingProduct.setProductModel(productRequest.getProductModel());
        }
        if (productRequest.getModelYear() > 0) {
            existingProduct.setModelYear(productRequest.getModelYear());
        }
        if (productRequest.getStartPrice() > 0) {
            existingProduct.setStartPrice(productRequest.getStartPrice());
        }
        if (productRequest.getPriceJump() > 0){
            existingProduct.setPriceJump(productRequest.getPriceJump());
        }
        if (productRequest.getDescription() != null) {
            existingProduct.setDescription(productRequest.getDescription());
        }
        if (productRequest.getAuctionDate() != null) {
            existingProduct.setAuctionDate(productRequest.getAuctionDate());
        }
        if (productRequest.getAuctionStartTime() != null) {
            existingProduct.setAuctionStartTime(productRequest.getAuctionStartTime());
        }
        if (productRequest.getAuctionDuration() > 0) {
            existingProduct.setAuctionDuration(productRequest.getAuctionDuration());
        }
        if (productRequest.getCategory() != null) {
            try {
                String categoryInput = productRequest.getCategory().toLowerCase();
                Product.Category categoryEnum;

                switch (categoryInput) {
                    case "antique":
                        categoryEnum = Product.Category.Antique;
                        break;
                    case "vintage":
                        categoryEnum = Product.Category.Vintage;
                        break;
                    case "classic":
                        categoryEnum = Product.Category.Classic;
                        break;
                    case "sports":
                        categoryEnum = Product.Category.Sports;
                        break;
                    case "luxury":
                        categoryEnum = Product.Category.Luxury;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid category: " + productRequest.getCategory() +
                            ". Valid categories are: Antique, Vintage, Classic, Sports, Luxury");
                }

                existingProduct.setCategory(categoryEnum);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid category: " + productRequest.getCategory() +
                    ". Valid categories are: Antique, Vintage, Classic, Sports, Luxury");
            }
        }
    }
}
