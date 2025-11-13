package com.onepiece.product_service.repo;

import com.onepiece.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.category = :category")
    List<Product> getProductsByCategory(Product.Category category);

    @Query("SELECT p FROM Product p WHERE p.sellerId = :sellerId")
    List<Product> getProductsBySellerId(int sellerId);

    boolean existsBySellerId(int sellerId);
}
