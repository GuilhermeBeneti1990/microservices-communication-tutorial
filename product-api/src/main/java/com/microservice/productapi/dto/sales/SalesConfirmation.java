package com.microservice.productapi.dto.sales;

import com.microservice.productapi.enums.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesConfirmation {

    private String salesId;
    private SalesStatus status;

}
