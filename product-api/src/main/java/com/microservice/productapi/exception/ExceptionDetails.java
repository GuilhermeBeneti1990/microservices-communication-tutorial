package com.microservice.productapi.exception;

import lombok.Data;

@Data
public class ExceptionDetails {

    private int status;
    private String message;

}
