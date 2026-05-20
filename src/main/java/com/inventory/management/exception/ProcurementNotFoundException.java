package com.inventory.management.exception;

public class ProcurementNotFoundException extends RuntimeException {

    public ProcurementNotFoundException(String message) {
        super(message);
    }
}