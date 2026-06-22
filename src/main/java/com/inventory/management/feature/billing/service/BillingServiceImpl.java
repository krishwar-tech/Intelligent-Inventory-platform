package com.inventory.management.feature.billing.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.inventory.management.common.enums.ProductStatus;
import com.inventory.management.common.exception.ProductNotFoundException;
import com.inventory.management.feature.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.management.config.TenantContext;

import com.inventory.management.feature.auth.entity.Tenant;
import com.inventory.management.feature.auth.repository.TenantRepository;

import com.inventory.management.feature.billing.dto.CartItemDto;
import com.inventory.management.feature.billing.dto.CheckoutRequest;
import com.inventory.management.feature.billing.dto.InvoiceResponse;
import com.inventory.management.feature.billing.entity.Sale;
import com.inventory.management.feature.billing.entity.SaleItem;
import com.inventory.management.feature.billing.repository.SaleItemRepository;
import com.inventory.management.feature.billing.repository.SaleRepository;


import com.inventory.management.feature.product.entity.Product;
import com.inventory.management.feature.product.repository.ProductRepository;

import com.inventory.management.common.enums.InventoryActionType;

@Service
public class BillingServiceImpl implements BillingService {

    private final SaleRepository saleRepo;

    private final SaleItemRepository itemRepo;

    private final ProductRepository productRepo;

    private final InventoryService inventoryService;

    private final TenantRepository tenantRepo;

    private static final Logger log =
            LoggerFactory.getLogger(BillingServiceImpl.class);

    public BillingServiceImpl(
            SaleRepository saleRepo,
            SaleItemRepository itemRepo,
            ProductRepository productRepo,
            InventoryService inventoryService,
            TenantRepository tenantRepo) {

        this.saleRepo = saleRepo;

        this.itemRepo = itemRepo;

        this.productRepo = productRepo;

        this.inventoryService = inventoryService;

        this.tenantRepo = tenantRepo;
    }

    @Override
    @Transactional
    public String checkout(CheckoutRequest req) {

        log.info("Checkout started");

        log.info("Customer : {}", req.getCustomerName());

        log.info("Payment mode : {}", req.getPaymentMode());

        Sale sale = new Sale();

        String billNo =
                "INV-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8);

        sale.setBillNo(billNo);

        sale.setCreatedAt(LocalDateTime.now());

        log.info("Generated bill number : {}", billNo);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItemDto item : req.getItems()) {

            BigDecimal itemTotal =
                    BigDecimal.valueOf(item.getPrice())
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQty()));

            subtotal = subtotal.add(itemTotal);
        }

        BigDecimal discount = value(req.getDiscount());

        BigDecimal taxable =
                subtotal.subtract(discount);

        BigDecimal tax =
                taxable.multiply(BigDecimal.valueOf(0.05));

        BigDecimal grandTotal =
                taxable.add(tax);

        BigDecimal cash =
                value(req.getCashPaid());

        BigDecimal upi =
                value(req.getUpiPaid());

        BigDecimal card =
                value(req.getCardPaid());

        BigDecimal paid =
                cash.add(upi).add(card);

        sale.setSubtotal(subtotal);

        sale.setDiscount(discount);

        sale.setTax(tax);

        sale.setGrandTotal(grandTotal);

        sale.setCustomerName(req.getCustomerName());

        sale.setCustomerMobile(req.getCustomerMobile());

        sale.setPaymentStatus(
                paid.compareTo(grandTotal) >= 0
                        ? "PAID"
                        : "PENDING");

        sale.setPaymentMode(req.getPaymentMode());

        sale.setCashPaid(cash);

        sale.setUpiPaid(upi);

        sale.setCardPaid(card);

        Tenant tenant =
                tenantRepo.findById(
                                TenantContext.getTenantId())
                        .orElseThrow();

        sale.setTenant(tenant);

        Sale savedSale =
                saleRepo.save(sale);

        List<SaleItem> items =
                new ArrayList<>();

        for (CartItemDto item : req.getItems()) {

            Product product;

            try {

                product =
                        productRepo.findByIdAndTenant_Id(
                                        item.getProductId(),
                                        TenantContext.getTenantId())
                                .orElseThrow(() ->
                                        new ProductNotFoundException(
                                                "Product not found"));

                log.info(
                        "Selling product : {} | Qty : {}",
                        product.getName(),
                        item.getQty());

            } catch (Exception e) {

                log.error(
                        "Product fetch failed for ID : {} | Reason : {}",
                        item.getProductId(),
                        e.getMessage());

                throw e;
            }

            if (product.getStatus() != ProductStatus.ACTIVE) {

                log.warn(
                        "Inactive product billing attempt : {}",
                        product.getName());

                throw new ProductNotFoundException(
                        product.getName() + " inactive");
            }

            inventoryService.decreaseStock(
                    product,
                    item.getQty(),
                    InventoryActionType.SALE,
                    "Sold");

            SaleItem si = new SaleItem();

            si.setSale(savedSale);

            si.setProduct(product);

            si.setQty(item.getQty());

            BigDecimal price =
                    BigDecimal.valueOf(item.getPrice());

            si.setPrice(price);

            si.setTotal(
                    price.multiply(
                            BigDecimal.valueOf(
                                    item.getQty())));

            items.add(si);
        }
        savedSale.setItems(items);
        saleRepo.save(savedSale);
        itemRepo.saveAll(items);

        log.info(
                "Invoice {} completed successfully. Grand Total : {}",
                billNo,
                grandTotal);

        return billNo;
    }

    @Override
    public InvoiceResponse getInvoice(Long saleId) {

        Sale sale =
                saleRepo.findById(saleId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Sale not found"));

        List<SaleItem> saleItems =
                itemRepo.findAll()
                        .stream()
                        .filter(i ->
                                i.getSale()
                                        .getId()
                                        .equals(saleId))
                        .toList();

        Map<String, Object> saleMap =
                new HashMap<>();

        saleMap.put("billNo", sale.getBillNo());

        saleMap.put("createdAt", sale.getCreatedAt());

        saleMap.put("paymentMode", sale.getPaymentMode());

        saleMap.put("totalAmount", sale.getGrandTotal());

        List<Map<String, Object>> items =
                new ArrayList<>();

        for (SaleItem si : saleItems) {

            Map<String, Object> m =
                    new HashMap<>();

            m.put(
                    "productName",
                    si.getProduct().getName());

            m.put("qty", si.getQty());

            m.put("total", si.getTotal());

            items.add(m);
        }

        InvoiceResponse response =
                new InvoiceResponse();

        response.setSale(saleMap);

        response.setItems(items);

        return response;
    }

    private BigDecimal value(Double val) {

        return val == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(val);
    }
}