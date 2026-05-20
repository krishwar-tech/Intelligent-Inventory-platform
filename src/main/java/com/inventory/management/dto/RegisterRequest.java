package com.inventory.management.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String companyName;

    private String username;

    private String password;

    private String email;

    private String phone;
}