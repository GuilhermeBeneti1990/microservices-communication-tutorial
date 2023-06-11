package com.microservice.productapi.service;

import com.microservice.productapi.config.client.SalesClient;
import com.microservice.productapi.config.rabbit.SalesConfirmationSender;
import com.microservice.productapi.dto.product.*;
import com.microservice.productapi.dto.response.SuccessResponse;
import com.microservice.productapi.dto.sales.ProductSalesResponse;
import com.microservice.productapi.dto.sales.SalesConfirmation;
import com.microservice.productapi.enums.SalesStatus;
import com.microservice.productapi.exception.ValidationException;
import com.microservice.productapi.model.Product;
import com.microservice.productapi.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@Slf4j
public class ProductService {

    private static final Integer ZERO = 0;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SalesConfirmationSender sender;

    @Autowired
    private SalesClient salesClient;

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
        try {
            validateStockUpdateData(product);
            updateStock(product);
        } catch(Exception ex) {
            log.error("Error while trying to update stock for message with error: {}", ex.getMessage(), ex);
            var rejectedMessage = new SalesConfirmation(product.getSalesId(), SalesStatus.REJECT);
            sender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }

    private void validateStockUpdateData(ProductStock product) {
        if(isEmpty(product) || isEmpty(product.getSalesId())) {
            throw new ValidationException("The product data and the sales must be informed!");
        }
        if(isEmpty(product.getProducts())) {
            throw new ValidationException("The sales products must be informed!");
        }
        product
                .getProducts()
                .forEach(salesProduct -> {
                    if(isEmpty(salesProduct.getQuantity()) || isEmpty(salesProduct.getProductId())) {
                        throw new ValidationException("The product id and the quantity must be informed!");
                    }
                });
    }

    @Transactional
    private void updateStock(ProductStock product) {
        var productsForUpdate = new ArrayList<Product>();
        product.getProducts()
                .forEach(salesProduct -> {
                    var existingProduct = findById(salesProduct.getProductId());
                    validateQuantityInStock(salesProduct, existingProduct);
                    existingProduct.updateStock(salesProduct.getQuantity());
                    productsForUpdate.add(existingProduct);
                });
        if(!isEmpty(productsForUpdate)) {
            repository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmation(product.getSalesId(), SalesStatus.APPROVED);
            sender.sendSalesConfirmationMessage(approvedMessage);
        }
    }

    private void validateQuantityInStock(ProductQuantity salesProduct, Product existingProduct) {
        if(salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("The product %s is out of stock!", existingProduct.getId())
            );
        }
    }

    public ProductSalesResponse findProductSales(Integer id) {
        var product = findById(id);
        try {
            var sales = salesClient
                    .findSalesByProductId(product.getId())
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product!"));
            return ProductSalesResponse.of(product, sales.getSalesIds());
        } catch(Exception ex) {
            throw new ValidationException("There was an error trying to get the product sales!");
        }
    }

    public SuccessResponse checkProductAvailable(ProductStockAvailableRequest request) {
        if(isEmpty(request) || isEmpty(request.getProducts())) {
            throw new ValidationException("The request data must be informed!");
        }
        request
                .getProducts()
                .forEach(this::validateStock);
        return SuccessResponse.create("The stock is ok for this product.");
    }

    private void validateStock(ProductQuantity productQuantity) {
        if(isEmpty(productQuantity.getProductId()) || isEmpty(productQuantity.getQuantity())) {
            throw new ValidationException("Product id and quantity must be informed!");
        }
        var product = findById(productQuantity.getProductId());
        if(productQuantity.getQuantity() > product.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("The product %s is out of stock!", product.getId())
            );
        }
    }

}
