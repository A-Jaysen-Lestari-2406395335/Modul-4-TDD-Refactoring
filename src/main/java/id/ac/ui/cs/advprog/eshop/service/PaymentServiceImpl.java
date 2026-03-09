package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.service.paymentmethod.CashOnDeliveryPaymentStrategy;
import id.ac.ui.cs.advprog.eshop.service.paymentmethod.PaymentMethodStrategy;
import id.ac.ui.cs.advprog.eshop.service.paymentmethod.VoucherCodePaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final Pattern NON_LETTER_PATTERN = Pattern.compile("[^A-Za-z]");

    private final PaymentRepository paymentRepository;
    private final Map<String, PaymentMethodStrategy> strategyByMethod;
    private final Map<String, Order> orderByPaymentId = new HashMap<>();

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, List<PaymentMethodStrategy> paymentMethodStrategies) {
        this.paymentRepository = paymentRepository;
        this.strategyByMethod = buildStrategyLookup(paymentMethodStrategies);
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        String status = determineInitialStatus(method, paymentData);
        Payment payment = new Payment(UUID.randomUUID().toString(), method, status, paymentData);
        orderByPaymentId.put(payment.getId(), order);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        syncOrderStatus(updatedPayment.getId(), status);
        return updatedPayment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private void syncOrderStatus(String paymentId, String paymentStatus) {
        Order relatedOrder = orderByPaymentId.get(paymentId);
        if (relatedOrder == null) {
            return;
        }

        if (STATUS_SUCCESS.equals(paymentStatus)) {
            relatedOrder.setStatus(OrderStatus.SUCCESS.getValue());
        } else if (STATUS_REJECTED.equals(paymentStatus)) {
            relatedOrder.setStatus(OrderStatus.FAILED.getValue());
        }
    }

    private String determineInitialStatus(String method, Map<String, String> paymentData) {
        PaymentMethodStrategy strategy = strategyByMethod.get(normalizeMethod(method));
        if (strategy == null) {
            throw new IllegalArgumentException();
        }
        return strategy.determineStatus(paymentData);
    }

    private String normalizeMethod(String method) {
        if (method == null) {
            throw new IllegalArgumentException();
        }
        return NON_LETTER_PATTERN.matcher(method).replaceAll("").toUpperCase();
    }

    private Map<String, PaymentMethodStrategy> buildStrategyLookup(List<PaymentMethodStrategy> paymentMethodStrategies) {
        List<PaymentMethodStrategy> availableStrategies = paymentMethodStrategies;
        if (availableStrategies == null || availableStrategies.isEmpty()) {
            availableStrategies = List.of(
                    new VoucherCodePaymentStrategy(),
                    new CashOnDeliveryPaymentStrategy()
            );
        }

        Map<String, PaymentMethodStrategy> lookup = new HashMap<>();
        for (PaymentMethodStrategy strategy : availableStrategies) {
            lookup.put(normalizeMethod(strategy.getMethodKey()), strategy);
        }
        return lookup;
    }
}
