package com.salestar.service;


import com.salestar.dto.ProductDto;
import com.salestar.entity.Order;
import com.salestar.entity.Product;
import com.salestar.repository.OrderRepository;
import com.salestar.repository.PaymentTransactionRepository;
import com.salestar.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final ApplicationContext context;
    private final OrderRepository orderRepository;

    public ProductServiceImpl(ProductRepository productRepository, PaymentTransactionRepository transactionRepository, ApplicationContext context, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.context = context;
        this.orderRepository = orderRepository;
    }

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

    @Override
    @Transactional
    public void purchaseProductOrchestration(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity)
            throw new RuntimeException("Not enough stock");

        Order order = saveOrder(product, quantity);

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        try {
            PaymentService paymentService = context.getBean(PaymentService.class);
            paymentService.processPayment(order, "CARD");
        } catch (Exception e) {
            order.setStatus("FAILED_PAYMENT");
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public void purchaseProductChoreography(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity)
            throw new RuntimeException("Not enough stock");

        Order order = saveOrder(product, quantity);

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // پیام غیرمستقیم: مثلا Event ارسال شود
        System.out.println("[Choreography] Payment event published.");
    }

    private Order saveOrder(Product product, int quantity) {
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }

}
