package com.quickcommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quickcommerce.entity.Product;
import java.util.List;
import java.util.Optional;

@Repository
public interface Productrepository extends JpaRepository<Product, Long> {

        Optional<Product> findBySku(String sku);

        List<Product> findByCategory(String category);

        @Query("SELECT p from Product p WHERE " +
                        "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword,'%')) OR " +
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword,'%'))) AND " +
                        "(:category IS NULL OR p.category = :category) AND " +
                        "p.status = 'ACTIVE'")
        List<Product> searchProducts(
                        @Param("keyword") String keyword,
                        @Param("category") String category);

        List<Product> findByStatus(Product.ProductStatus status);

        @Query("SELECT p FROM Product p WHERE p.quantity < :threshold")
        List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}
