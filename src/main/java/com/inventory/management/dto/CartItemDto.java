package com.inventory.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1,
            message = "Quantity must be at least 1")
    private Integer qty;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;
}