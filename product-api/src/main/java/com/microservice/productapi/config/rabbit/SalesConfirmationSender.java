package com.microservice.productapi.config.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.productapi.dto.sales.SalesConfirmation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesConfirmationSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${api-config.rabbit.exchange.product}")
    private String productTopicExchange;

    @Value("${api-config.rabbit.routingKey.sales-confirmation}")
    private String salesConfirmationKey;

    public void sendSalesConfirmationMessage(SalesConfirmation message) {
        try {
            log.info("Sending message: {}", new ObjectMapper().writeValueAsString(message));
            rabbitTemplate.convertAndSend(productTopicExchange, salesConfirmationKey, message);
            log.info("Message was sent successfully.");
        } catch(Exception ex) {
            log.error("Error while trying to send sales confirmation message: {}", ex);
        }
    }

}
