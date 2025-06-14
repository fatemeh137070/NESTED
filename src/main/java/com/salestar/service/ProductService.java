package com.salestar.service;


import com.salestar.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto dto);
    List<ProductDto> getAllProducts();
    void reduceStock(Long productId, int quantity);
}