package com.salestar;

import com.salestar.entity.Order;
import com.salestar.entity.Product;
import com.salestar.repository.OrderRepository;
import com.salestar.repository.ProductRepository;
import com.salestar.service.PaymentTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.salestar.entity.Product;
import com.salestar.entity.Order;
import com.salestar.repository.OrderRepository;
import com.salestar.repository.PaymentTransactionRepository;
import com.salestar.repository.ProductRepository;
import com.salestar.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest
class SalesTarApplicationTests {

//    @Autowired
//    private PaymentTransactionService paymentTransactionService;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Test
//    void testProcessPaymentWithRequiresNew() {
//        // 1. ساخت محصول
//        Product product = new Product();
//        product.setName("Test Product");
//        product.setPrice(new BigDecimal("100000"));
//        product.setStock(10);
//        product = productRepository.save(product);
//
//        // 2. ساخت سفارش
//        Order order = new Order();
//        order.setOrderDate(LocalDateTime.now());
//        order.setProduct(product);
//        order.setQuantity(2);
//        order.setStatus("PENDING");
//        order = orderRepository.save(order);
//
//        // 3. اجرای پرداخت (که تراکنش REQUIRES_NEW داره و عمدی خطا میده)
//        try {
//            paymentTransactionService.processPayment(order, "CREDIT_CARD");
//        } catch (Exception e) {
//            System.out.println("Exception during payment: " + e.getMessage());
//        }
//
//        // 4. بررسی اینکه سفارش هنوز در دیتابیس هست
//        assert orderRepository.findById(order.getId()).isPresent();
//    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentTransactionRepository transactionRepository;

    @Autowired
    private PaymentService paymentService;

    private Product savedProduct;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();

        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);

        savedProduct = productRepository.save(product);
    }

    @Test
    void testPurchaseProduct_withNestedRollback() {
        try {
            paymentService.purchaseProduct(savedProduct.getId(), 2);
        } catch (Exception e) {
            // نباید اینجا خطا بیاد چون فقط متد داخل rollback میشه
            System.err.println("نباید اینجا باشیم!");
        }

        // 💥 انتظار داریم فقط پرداخت rollback بشه

        // 🟢 سفارش ثبت شده باشه
        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo("FAILED_PAYMENT");

        // 🔴 پرداخت ثبت نشده باشه چون nested rollback شده
        assertThat(transactionRepository.findAll()).isEmpty();

        // 🟢 موجودی محصول کم شده باشه
        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(8);
    }
}
