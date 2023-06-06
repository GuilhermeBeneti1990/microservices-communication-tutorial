package com.microservice.productapi.model;

import com.microservice.productapi.dto.product.ProductRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "FK_CATEGORY", nullable = false)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "FK_SUPPLIER", nullable = false)
    private Supplier supplier;
    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static Product of(ProductRequest request, Supplier supplier, Category category) {
        return Product
                .builder()
                .name(request.getName())
                .quantityAvailable(request.getQuantityAvailable())
                .category(category)
                .supplier(supplier)
                .build();
    }

}
