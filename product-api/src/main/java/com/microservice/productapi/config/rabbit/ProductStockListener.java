package com.microservice.productapi.config.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.productapi.dto.product.ProductStock;
import com.microservice.productapi.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductStockListener {

    @Autowired
    private ProductService service;

    @RabbitListener(queues = "${api-config.rabbit.queue.product-stock}")
    public void receiveProductStockMessage(ProductStock product) throws JsonProcessingException {
        log.info("Receiving message: {}", new ObjectMapper().writeValueAsString(product));
        service.updateProductStock(product);
    }

}
