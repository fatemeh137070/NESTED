package com.salestar.service;

import com.salestar.entity.Order;
import com.salestar.entity.PaymentTransaction;
import com.salestar.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository transactionRepository;

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @Override
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
//        // شبیه‌سازی خطا
////        throw new RuntimeException("پرداخت ناموفق بود");
//    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public void processPayment(Order order, String method) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setOrder(order);
        tx.setAmount(order.getProduct().getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        tx.setMethod(method);
        tx.setStatus("SUCCESS");
        tx.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(tx);

        // شبیه‌سازی خطا برای rollback nested
//        if (false) {
//            throw new RuntimeException("پرداخت با خطا مواجه شد!");
//        }
    }



}
