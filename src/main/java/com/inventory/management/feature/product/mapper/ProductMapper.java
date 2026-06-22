package com.inventory.management.feature.product.mapper;

import java.util.List;

import com.inventory.management.feature.product.dto.ProductDto;
import com.inventory.management.feature.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);

    List<ProductDto> toDtoList(List<Product> products);
}