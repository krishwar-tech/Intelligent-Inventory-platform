package com.inventory.management.feature.billing.service;

import com.inventory.management.feature.billing.dto.CheckoutRequest;
import com.inventory.management.feature.billing.dto.InvoiceResponse;

public interface BillingService {

	String checkout(CheckoutRequest req);

	InvoiceResponse getInvoice(Long saleId);
}