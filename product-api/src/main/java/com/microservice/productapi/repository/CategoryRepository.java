package com.microservice.productapi.repository;

import com.microservice.productapi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByDescription(String description);

}
