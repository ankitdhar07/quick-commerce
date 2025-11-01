package com.quickcommerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quickcommerce.dto.ProductDTO;
import com.quickcommerce.entity.Product;
import com.quickcommerce.repository.Productrepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final Productrepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProductDTO createProduct(ProductDTO productDTO) {

        log.info("Creating product: {}", productDTO.getSku());

        Product product = Product.builder()
                .sku(productDTO.getSku())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .category(productDTO.getCategory())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created with ID: {}", saved.getId());
        publishProductEvent("Product Created", saved);
        return ProductDTO.from(saved);

    }

    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        return ProductDTO.from(product);
    }

    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found: " + sku));
        return ProductDTO.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String keyword, String category) {
        return productRepository.searchProducts(keyword, category).stream()
                .map(ProductDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(ProductDTO::from)
                .collect(Collectors.toList());
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setCategory(productDTO.getCategory());

        Product updated = productRepository.save(product);
        publishProductEvent("ProductUpdated", updated);
        return ProductDTO.from(updated);
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        productRepository.delete(product);
        publishProductEvent("ProductDeleted", product);
    }

    public void updateStock(Long productId, Integer quantity) {
        log.info("Updating stock for product: {} with quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        publishProductEvent("InventoryChanged", product);
    }

    private void publishProductEvent(String eventType, Product product) {
        String message = String.format(
                "{\"eventType\":\"%s\",\"productId\":%d,\"sku\":\"%s\",\"name\":\"%s\",\"quantity\":%d}",
                eventType, product.getId(), product.getSku(), product.getName(), product.getQuantity());
        try {
            kafkaTemplate.send("product-events", product.getId().toString(), message);
            log.info("Published {} event for product: {}", eventType, product.getId());
        } catch (Exception e) {
            log.error("Failed to publish event", e);
        }
    }
}