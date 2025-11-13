package com.onepiece.product_service.controller;

import com.onepiece.product_service.model.ProductImage;
import com.onepiece.product_service.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product-service/product-images")
@CrossOrigin
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductImages(@PathVariable int productId) {
        try {
            List<ProductImage> images = productImageService.getImagesByProductId(productId);
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching images: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<?> getProductImageById(@PathVariable int imageId) {
        try {
            ProductImage image = productImageService.getImageById(imageId);
            if (image != null) {
                return new ResponseEntity<>(image, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addProductImage(
            @PathVariable int productId,
            @RequestPart MultipartFile imageFile) {
        try {
            ProductImage savedImage = productImageService.saveProductImage(productId, imageFile);
            return new ResponseEntity<>(savedImage, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing image: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-multiple/{productId}")
    public ResponseEntity<?> addMultipleProductImages(
            @PathVariable int productId,
            @RequestPart List<MultipartFile> imageFiles) {
        try {
            List<ProductImage> savedImages = productImageService.addMultipleImages(productId, imageFiles);
            return new ResponseEntity<>(savedImages, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing images: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{imageId}")
    public ResponseEntity<?> updateProductImage(
            @PathVariable int imageId,
            @RequestPart MultipartFile imageFile) {
        try {
            ProductImage updatedImage = productImageService.updateProductImage(imageId, imageFile);
            return new ResponseEntity<>(updatedImage, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing image: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Image not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<?> deleteProductImage(@PathVariable int imageId) {
        try {
            productImageService.deleteImageById(imageId);
            return new ResponseEntity<>("Image deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-all/{productId}")
    public ResponseEntity<?> deleteAllProductImages(@PathVariable int productId) {
        try {
            productImageService.deleteImagesByProductId(productId);
            return new ResponseEntity<>("All images deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting images: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/view/{imageId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImageBytes(@PathVariable int imageId) {
        try {
            ProductImage image = productImageService.getImageById(imageId);
            if (image != null && image.getImageData() != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(image.getImageData());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}