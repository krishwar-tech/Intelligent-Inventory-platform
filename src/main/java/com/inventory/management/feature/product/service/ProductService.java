package com.inventory.management.feature.product.service;


import com.inventory.management.feature.product.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {

	Product save(Product p);

	List<Product> getAll();

	Product getById(Long id);

	Product getByBarcode(String code);

	List<Product> getActive();

	List<Product> getPending();

	Map<String, Object> stats();

	List<Product> getByCategory(Long categoryId);

	List<Product> search(String keyword);

	Product update(Long id, Product body, Long categoryId, Long subCategoryId);

	Product updatePrice(Long id, Double price, Double mrp);

	Product approve(Long id, Double price, Double mrp);

	Product delete(Long id);

	Product activate(Long id);

	Product deactivate(Long id);
}