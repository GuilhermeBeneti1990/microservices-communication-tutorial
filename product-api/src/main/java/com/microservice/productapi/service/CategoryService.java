package com.microservice.productapi.service;

import com.microservice.productapi.dto.category.CategoryRequest;
import com.microservice.productapi.dto.category.CategoryResponse;
import com.microservice.productapi.dto.response.SuccessResponse;
import com.microservice.productapi.exception.ValidationException;
import com.microservice.productapi.model.Category;
import com.microservice.productapi.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private ProductService productService;

    public List<CategoryResponse> findAll() {
        return repository
                .findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public CategoryResponse findByIdResponse(Integer id) {
        return CategoryResponse.of(findById(id));
    }

    public CategoryResponse findByDescriptionResponse(String description) {
        return CategoryResponse.of(findByDescription(description));
    }

    public CategoryResponse save(CategoryRequest request) {
        validateCategoryDescriptionInformed(request);
        var category = repository.save(Category.of(request));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest request, Integer id) {
        validateCategoryDescriptionInformed(request);
        validateEmptyId(id);
        var category = Category.of(request);
        category.setId(id);
        repository.save(category);
        return CategoryResponse.of(category);
    }

    private void validateCategoryDescriptionInformed(CategoryRequest request) {
        if(isEmpty(request.getDescription())) {
            throw new ValidationException("The category description was not informed!");
        }
    }

    public Category findById(Integer id) {
        validateEmptyId(id);
        return repository
                .findById(id)
                .orElseThrow(() -> new ValidationException("There's no category for the given Id!"));
    }

    public Category findByDescription(String description) {
        if(isEmpty(description)) {
            throw new ValidationException("The category description was not informed!");
        }
        return repository
                .findByDescription(description);
    }

    public SuccessResponse delete(Integer id) {
        validateEmptyId(id);
        if(productService.existsByCategoryId(id)) {
            throw new ValidationException("You cannot delete this category, because it's defined by a product");
        }
        repository.deleteById(id);
        return SuccessResponse.create("The category was deleted.");
    }

    public void validateEmptyId(Integer id) {
        if(isEmpty(id)) {
            throw new ValidationException("The category id was not informed!");
        }
    }

}
