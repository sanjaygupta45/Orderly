package com.hello.microservice.product_service.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductFilterRequest {
    private int page;
    private int size;
    private String category;
}