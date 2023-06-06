package com.microservice.productapi.service;

import com.microservice.productapi.dto.response.SuccessResponse;
import com.microservice.productapi.dto.supplier.SupplierRequest;
import com.microservice.productapi.dto.supplier.SupplierResponse;
import com.microservice.productapi.exception.ValidationException;
import com.microservice.productapi.model.Supplier;
import com.microservice.productapi.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository repository;

    @Autowired
    private ProductService productService;

    public List<SupplierResponse> findAll() {
        return repository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id) {
        return SupplierResponse.of(findById(id));
    }

    public SupplierResponse findByNameResponse(String name) {
        return SupplierResponse.of(findByName(name));
    }

    public SupplierResponse save(SupplierRequest request) {
        validateSupplerNameInformed(request);
        var supplier = repository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request, Integer id) {
        validateSupplerNameInformed(request);
        validateEmptyId(id);
        var supplier = Supplier.of(request);
        supplier.setId(id);
        repository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    public Supplier findById(Integer id) {
        validateEmptyId(id);
        return repository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no supplier for the given Id!"));
    }

    public Supplier findByName(String name) {
        if(isEmpty(name)) {
            throw new ValidationException("The supplier name was not informed!");
        }
        return repository
                .findByName(name);
    }

    private void validateSupplerNameInformed(SupplierRequest request) {
        if(isEmpty(request.getName())) {
            throw new ValidationException("The supplier name was not informed!");
        }
    }

    public SuccessResponse delete(Integer id) {
        validateEmptyId(id);
        if(productService.existsBySupplierId(id)) {
            throw new ValidationException("You cannot delete this supplier, because it's defined by a product");
        }
        repository.deleteById(id);
        return SuccessResponse.create("The supplier was deleted.");
    }

    public void validateEmptyId(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException("The supplier id was not informed!");
        }
    }

}
