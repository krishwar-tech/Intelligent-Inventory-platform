package com.inventory.management.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private int status;

    private T data;

    private LocalDateTime timestamp;
}