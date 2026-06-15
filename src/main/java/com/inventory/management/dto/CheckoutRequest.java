package com.inventory.management.dto;

import java.util.List;

import com.inventory.management.enums.PaymentMode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CheckoutRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Pattern(
            regexp = "^(|\\d{10})$",
            message = "Customer mobile must contain 10 digits")
    private String customerMobile;

    private PaymentMode paymentMode;

    @PositiveOrZero(message = "Discount cannot be negative")
    private Double discount;

    @PositiveOrZero(message = "Cash amount cannot be negative")
    private Double cashPaid;

    @PositiveOrZero(message = "UPI amount cannot be negative")
    private Double upiPaid;

    @PositiveOrZero(message = "Card amount cannot be negative")
    private Double cardPaid;

    @Valid
    @NotEmpty(message = "Cart items cannot be empty")
    private List<CartItemDto> items;

}