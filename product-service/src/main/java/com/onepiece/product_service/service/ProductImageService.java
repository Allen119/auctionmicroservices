package com.onepiece.product_service.service;


import com.onepiece.product_service.model.ProductImage;
import com.onepiece.product_service.repo.ProductImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepo productImageRepo;

    public ProductImage saveProductImage(int productId, MultipartFile imageFile) throws IOException {
        ProductImage productImage = new ProductImage();
        productImage.setProductId(productId);
        productImage.setImageData(imageFile.getBytes());
        return productImageRepo.save(productImage);
    }

    public List<ProductImage> getImagesByProductId(int productId) {
        return productImageRepo.findByProductId(productId);
    }

    public void deleteImagesByProductId(int productId) {
        productImageRepo.deleteByProductId(productId);
    }

    public ProductImage getImageById(int imageId) {
        return productImageRepo.findById(imageId).orElse(null);
    }

    public void deleteImageById(int imageId) {
        productImageRepo.deleteById(imageId);
    }

    public ProductImage updateProductImage(int imageId, MultipartFile imageFile) throws IOException {
        ProductImage existingImage = productImageRepo.findById(imageId).orElse(null);
        if (existingImage != null) {
            existingImage.setImageData(imageFile.getBytes());
            return productImageRepo.save(existingImage);
        }
        throw new RuntimeException("Image not found with ID: " + imageId);
    }

    public List<ProductImage> addMultipleImages(int productId, List<MultipartFile> imageFiles) throws IOException {
        List<ProductImage> savedImages = new java.util.ArrayList<>();
        final long MAX_IMAGE_SIZE = 16 * 1024 * 1024;

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile != null && !imageFile.isEmpty()) {
                if (imageFile.getSize() > MAX_IMAGE_SIZE) {
                    throw new IllegalArgumentException("Image size exceeds 16MB limit. Current size: " +
                        (imageFile.getSize() / (1024 * 1024)) + "MB");
                }
                ProductImage productImage = new ProductImage();
                productImage.setProductId(productId);
                productImage.setImageData(imageFile.getBytes());
                savedImages.add(productImageRepo.save(productImage));
            }
        }
        return savedImages;
    }
}
