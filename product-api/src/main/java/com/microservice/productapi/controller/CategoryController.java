package com.microservice.productapi.controller;

import com.microservice.productapi.dto.category.CategoryRequest;
import com.microservice.productapi.dto.category.CategoryResponse;
import com.microservice.productapi.dto.response.SuccessResponse;
import com.microservice.productapi.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @PostMapping
    public CategoryResponse save(@RequestBody CategoryRequest request) {
        return service.save(request);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Integer id) {
        return service.findByIdResponse(id);
    }

    @GetMapping("/description/{description}")
    public CategoryResponse findById(@PathVariable String description) {
        return service.findByDescriptionResponse(description);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@RequestBody CategoryRequest request, @PathVariable Integer id) {
        return service.update(request, id);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return service.delete(id);
    }

}
