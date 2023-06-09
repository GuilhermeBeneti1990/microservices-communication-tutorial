package com.microservice.productapi.config.client;

import com.microservice.productapi.dto.sales.SalesProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "salesClient",
        contextId = "salesClient",
        url = "${api-config.services.sales}"
)
public interface SalesClient {

    @GetMapping("/api/orders/products/{productId}")
    Optional<SalesProductResponse> findSalesByProductId(@PathVariable Integer productId);

}
