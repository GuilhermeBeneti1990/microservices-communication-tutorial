package com.microservice.productapi.service;

import com.microservice.productapi.dto.product.ProductRequest;
import com.microservice.productapi.dto.product.ProductResponse;
import com.microservice.productapi.dto.product.ProductStock;
import com.microservice.productapi.dto.response.SuccessResponse;
import com.microservice.productapi.exception.ValidationException;
import com.microservice.productapi.model.Product;
import com.microservice.productapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CategoryService categoryService;

    public ProductResponse save(ProductRequest request) {
        validateProductDataInformed(request);
        validateCategoryAndSupplierInformed(request);
        var category =  categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = repository.save(Product.of(request, supplier, category));
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id) {
        validateProductDataInformed(request);
        validateCategoryAndSupplierInformed(request);
        validateEmptyId(id);
        var category =  categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product.setId(id);
        repository.save(product);
        return ProductResponse.of(product);
    }

    public List<ProductResponse> findAll() {
        return repository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(findById(id));
    }

    public ProductResponse findByNameResponse(String name) {
        return ProductResponse.of(findByName(name));
    }

    public Product findById(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException("The product id was not informed!");
        }
        return repository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no product for the given Id!"));
    }

    public Product findByName(String name) {
        if(isEmpty(name)) {
            throw new ValidationException("The supplier name was not informed!");
        }
        return repository
                .findByName(name);
    }

    public List<ProductResponse> findByCategory(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException("The category was not informed!");
        }
        return repository
                .findByCategoryId(id)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplier(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException("The supplier was not informed!");
        }
        return repository
                .findBySupplierId(id)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    private void validateProductDataInformed(ProductRequest request) {
        if(isEmpty(request.getName())) {
            throw new ValidationException("The product name was not informed!");
        }
        if(isEmpty(request.getQuantityAvailable())) {
            throw new ValidationException("The product quantity was not informed!");
        }
        if(request.getQuantityAvailable() <= ZERO) {
            throw new ValidationException("The quantity should not be less or equal to zero!");
        }
    }

    private void validateCategoryAndSupplierInformed(ProductRequest request) {
        if(isEmpty(request.getCategoryId())) {
            throw new ValidationException("The category was not informed!");
        }
        if(isEmpty(request.getSupplierId())) {
            throw new ValidationException("The supplier was not informed!");
        }
    }

    public Boolean existsByCategoryId(Integer id) {
        return repository.existsByCategoryId(id);
    }

    public Boolean existsBySupplierId(Integer id) {
        return repository.existsBySupplierId(id);
    }

    public SuccessResponse delete(Integer id) {
        validateEmptyId(id);
        repository.deleteById(id);
        return SuccessResponse.create("The product was deleted.");
    }

    public void validateEmptyId(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException("The product id was not informed!");
        }
    }

    public void updateProductStock(ProductStock product) {

    }

}
