package com.inventory.management.common.exception;

public class ProcurementNotFoundException extends RuntimeException {

    public ProcurementNotFoundException(String message) {
        super(message);
    }
}