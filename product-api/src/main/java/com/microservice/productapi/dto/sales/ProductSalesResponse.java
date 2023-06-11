package com.microservice.productapi.dto.sales;

import com.microservice.productapi.dto.category.CategoryResponse;
import com.microservice.productapi.dto.product.ProductResponse;
import com.microservice.productapi.dto.supplier.SupplierResponse;
import com.microservice.productapi.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesResponse {

    private Integer id;
    private String name;
    private Integer quantityAvailable;
    private LocalDateTime createdAt;
    private SupplierResponse supplier;
    private CategoryResponse category;
    private List<String> sales;

    public static ProductSalesResponse of(Product product, List<String> sales) {
        return ProductSalesResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .quantityAvailable(product.getQuantityAvailable())
                .supplier(SupplierResponse.of(product.getSupplier()))
                .category(CategoryResponse.of(product.getCategory()))
                .createdAt(product.getCreatedAt())
                .sales(sales)
                .build();
    }

}
