package com.microservice.productapi.model;

import com.microservice.productapi.dto.category.CategoryRequest;
import com.microservice.productapi.dto.supplier.SupplierRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;

    public static Supplier of(SupplierRequest request) {
        var supplier = new Supplier();
        BeanUtils.copyProperties(request, supplier);
        return supplier;
    }

}
