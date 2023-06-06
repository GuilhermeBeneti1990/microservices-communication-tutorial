package com.microservice.productapi.repository;

import com.microservice.productapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByName(String name);

    List<Product> findByCategoryId(Integer id);

    List<Product> findBySupplierId(Integer id);

    Boolean existsByCategoryId(Integer id);

    Boolean existsBySupplierId(Integer id);

}
