package com.inventory.management.service;

import com.inventory.management.dto.CheckoutRequest;
import com.inventory.management.dto.InvoiceResponse;

public interface BillingService {

	String checkout(CheckoutRequest req);

	InvoiceResponse getInvoice(Long saleId);
}