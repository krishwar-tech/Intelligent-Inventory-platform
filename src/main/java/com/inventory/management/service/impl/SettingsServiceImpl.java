package com.inventory.management.service.impl;

import com.inventory.management.entity.Product;
import com.inventory.management.entity.Settings;
import com.inventory.management.repository.ProductRepository;
import com.inventory.management.repository.SettingsRepository;
import com.inventory.management.service.SettingsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository repo;

    private final ProductRepository productRepo;

    private static final Logger log =
            LoggerFactory.getLogger(
                    SettingsServiceImpl.class);

    public SettingsServiceImpl(
            SettingsRepository repo,
            ProductRepository productRepo) {

        this.repo = repo;
        this.productRepo = productRepo;
    }

    @Override
    public Settings getSettings() {

        log.info(
                "Fetching application settings");

        return repo.findById(1L)
                .orElseGet(() -> {

                    log.warn(
                            "Settings not found, creating default settings");

                    Settings s =
                            new Settings();

                    s.setSafetyStock(5);

                    s.setReorderLevel(10);

                    s.setStoreName("StockFlow");

                    s.setCurrencySymbol("₹");

                    Settings saved =
                            repo.save(s);

                    log.info(
                            "Default settings created successfully");

                    return saved;
                });
    }

    @Override
    public Settings save(Settings input) {

        log.info("Updating application settings");

        input.setId(1L);

        Settings saved = repo.save(input);

        List<Product> products = productRepo.findAll();

        for (Product product : products) {

            product.setSafetyStock(
                    saved.getSafetyStock());

            product.setReorderLevel(
                    saved.getReorderLevel());
        }

        productRepo.saveAll(products);

        log.info("Settings updated successfully");

        return saved;
    }
}