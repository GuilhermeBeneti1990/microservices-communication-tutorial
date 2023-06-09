package com.microservice.productapi.repository;

import com.microservice.productapi.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    Supplier findByName(String name);

}
