package com.microservice.productapi.controller;

import com.microservice.productapi.dto.product.ProductRequest;
import com.microservice.productapi.dto.product.ProductResponse;
import com.microservice.productapi.dto.product.ProductStockAvailableRequest;
import com.microservice.productapi.dto.response.SuccessResponse;
import com.microservice.productapi.dto.sales.ProductSalesResponse;
import com.microservice.productapi.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest request) {
        return service.save(request);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Integer id) {
        return service.findByIdResponse(id);
    }

    @GetMapping("/name/{name}")
    public ProductResponse findById(@PathVariable String name) {
        return service.findByNameResponse(name);
    }

    @GetMapping("/category/{category}")
    public List<ProductResponse> findByCategory(@PathVariable Integer category) {
        return service.findByCategory(category);
    }

    @GetMapping("/supplier/{supplier}")
    public List<ProductResponse> findBySupplier(@PathVariable Integer supplier) {
        return service.findBySupplier(supplier);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@RequestBody ProductRequest request, @PathVariable Integer id) {
        return service.update(request, id);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return service.delete(id);
    }

    @PostMapping("/stock")
    public SuccessResponse checkProductAvailable(@RequestBody ProductStockAvailableRequest request) {
        return service.checkProductAvailable(request);
    }

    @GetMapping("/{id}/sales")
    public ProductSalesResponse findProductSales(@PathVariable Integer id) {
        return service.findProductSales(id);
    }

}
