package com.salestar.service;


import com.salestar.dto.OrderSummaryDto;
import com.salestar.entity.Order;
import com.salestar.entity.PaymentTransaction;
import com.salestar.entity.Product;
import com.salestar.repository.OrderRepository;
import com.salestar.repository.PaymentTransactionRepository;
import com.salestar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ApplicationContext applicationContext; // ✅ اضافه شده
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository transactionRepository;


    //تراکنش به صورت اتومیک هست

//    @Override
//    @Transactional
//    public void purchaseProduct(Long productId, int quantity) {
//        // Step 1: Find product
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        // Step 2: Check stock
//        if (product.getStock() < quantity) {
//            throw new RuntimeException("Not enough stock");
//        }
//
//        // Step 3: Create Order
//        Order order = new Order();
//        order.setProduct(product);
//        order.setQuantity(quantity);
//        order.setOrderDate(LocalDateTime.now());
//        order.setStatus("PAID"); // یا PENDING
//        orderRepository.save(order);
//
//        // Step 4: Reduce Stock
//        product.setStock(product.getStock() - quantity);
//        productRepository.save(product);
//
//        // Step 5: Create Payment Transaction
//        PaymentTransaction transaction = new PaymentTransaction();
//        transaction.setOrder(order);
//        transaction.setAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
//        transaction.setMethod("CREDIT_CARD"); // یا PAYPAL
//        transaction.setStatus("SUCCESS"); // یا FAILED
//        transaction.setTransactionDate(LocalDateTime.now());
//        transactionRepository.save(transaction);
//    }


    //حالا اگرمتد اصلی ما تراکنش انجام میده و بره داخل تراکنش دوم متد دوم این کلش رول بک میشه بخاطره requared هست

//
//    @Override
//    @Transactional
//    public void purchaseProduct(Long productId, int quantity) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        if (product.getStock() < quantity) {
//            throw new RuntimeException("Not enough stock");
//        }
//
//        Order order = new Order();
//        order.setProduct(product);
//        order.setQuantity(quantity);
//        order.setOrderDate(LocalDateTime.now());
//        order.setStatus("PAID");
//        orderRepository.save(order);
//
//        product.setStock(product.getStock() - quantity);
//        productRepository.save(product);
//
//        // حالا متد تراکنش تو در تو را صدا می‌زنیم
//        processPayment(order, "CREDIT_CARD");
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.REQUIRED) // یا حذف کامل چون default همین هست
//    public void processPayment(Order order, String method) {
//        PaymentTransaction tx = new PaymentTransaction();
//        tx.setOrder(order);
//        tx.setAmount(order.getProduct().getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
//        tx.setMethod(method);
//        tx.setStatus("SUCCESS");
//        tx.setTransactionDate(LocalDateTime.now());
//
//        transactionRepository.save(tx);
//
//        // شبیه‌سازی خطا برای تست rollback همه چیز
//        if (true) {
//            throw new RuntimeException("پرداخت با خطا مواجه شد!");
//        }
//
//    }

    //REQUIRES_NE این یعنی تستش به این شکل هست که باید اول متد اول و استپ می شه بعد متد دوم کارش انجام می شه بعد می ره سراغ متد اول کارش و تموم می کنه

//    @Override
//    @Transactional
//    public void purchaseProduct(Long productId, int quantity) {
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        if (product.getStock() < quantity) {
//            throw new RuntimeException("Not enough stock");
//        }
//
//        // ثبت سفارش با تراکنش جدید
//        Order order = saveOrder(product, quantity);
//
//        // کاهش موجودی در تراکنش اصلی
//        product.setStock(product.getStock() - quantity);
//        productRepository.save(product);
//
//        // صدا زدن پردازش پرداخت با خطاگیری
//        PaymentTransactionService proxy = applicationContext.getBean(PaymentTransactionService.class);
//        try {
//            proxy.processPayment(order, "CREDIT_CARD");
//        } catch (Exception e) {
//            // ✅ فقط خطای پرداخت را هندل می‌کنیم
//            System.err.println("خطا در پرداخت: " + e.getMessage());
//
//            // ⚠️ به‌روزرسانی وضعیت سفارش به FAILED_PAYMENT (اختیاری)
//            order.setStatus("FAILED_PAYMENT");
//            orderRepository.save(order); // این ذخیره در تراکنش اصلی خواهد بود
//        }
//    }
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public Order saveOrder(Product product, int quantity) {
//        Order order = new Order();
//        order.setProduct(product);
//        order.setQuantity(quantity);
//        order.setOrderDate(LocalDateTime.now());
//        order.setStatus("PAID");
//        return orderRepository.save(order);
//    }

    @Override
    @Transactional
    public void purchaseProduct(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        // سفارش
        Order order = saveOrder(product, quantity);

        // کاهش موجودی
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // پردازش پرداخت با NESTED
        PaymentTransactionService proxy = applicationContext.getBean(PaymentTransactionService.class);
        try {
            proxy.processPayment(order, "CREDIT_CARD");
        } catch (Exception e) {
            System.err.println("❌ خطا در پرداخت (Nested Rollback فقط روی تراکنش داخلی): " + e.getMessage());

            // وضعیت سفارش رو تغییر بده
            order.setStatus("FAILED_PAYMENT");
            orderRepository.save(order);
        }
    }


    public Order saveOrder(Product product, int quantity) {
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PAID");
        return orderRepository.save(order);
    }


    @Override
    public List<OrderSummaryDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(order -> {
            OrderSummaryDto dto = new OrderSummaryDto();
            dto.setOrderId(order.getId());
            dto.setProductName(order.getProduct().getName());
            dto.setQuantity(order.getQuantity());
            dto.setStatus(order.getStatus());
            dto.setOrderDate(order.getOrderDate());

            // پیدا کردن پرداخت مرتبط (در صورت وجود)
            PaymentTransaction tx = transactionRepository.findByOrder(order).orElse(null);
            if (tx != null) {
                dto.setPaymentMethod(tx.getMethod());
                dto.setPaymentAmount(tx.getAmount());
                dto.setPaymentStatus(tx.getStatus());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}





