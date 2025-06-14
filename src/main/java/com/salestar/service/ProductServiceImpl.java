package com.salestar.service;


import com.salestar.dto.ProductDto;
import com.salestar.entity.Product;
import com.salestar.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDto createProduct(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return toDto(productRepository.save(product));
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

//    @Override
//    public void reduceStock(Long productId, int quantity) {
//        Product product = productRepository.findById(productId).orElseThrow();
//        product.setStock(product.getStock() - quantity);
//        productRepository.save(product);
//    }

    private ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        return dto;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)  // nested
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow();
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

}
