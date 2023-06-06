package com.microservice.productapi.controller;

import com.microservice.productapi.dto.category.CategoryRequest;
import com.microservice.productapi.dto.category.CategoryResponse;
import com.microservice.productapi.dto.response.SuccessResponse;
import com.microservice.productapi.dto.supplier.SupplierRequest;
import com.microservice.productapi.dto.supplier.SupplierResponse;
import com.microservice.productapi.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @PostMapping
    public SupplierResponse save(@RequestBody SupplierRequest request) {
        return service.save(request);
    }

    @GetMapping
    public List<SupplierResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public SupplierResponse findById(@PathVariable Integer id) {
        return service.findByIdResponse(id);
    }

    @GetMapping("/name/{name}")
    public SupplierResponse findById(@PathVariable String name) {
        return service.findByNameResponse(name);
    }

    @PutMapping("/{id}")
    public SupplierResponse update(@RequestBody SupplierRequest request, @PathVariable Integer id) {
        return service.update(request, id);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return service.delete(id);
    }

}
