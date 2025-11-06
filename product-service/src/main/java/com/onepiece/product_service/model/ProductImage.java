package com.onepiece.product_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "product_images")
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private int imageId;

    @Column(name = "product_id")
    private int productId;

    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    @Lob
    private byte[] imageData;
    
    @Column(name = "image_url")
    private String imageUrl;
}
