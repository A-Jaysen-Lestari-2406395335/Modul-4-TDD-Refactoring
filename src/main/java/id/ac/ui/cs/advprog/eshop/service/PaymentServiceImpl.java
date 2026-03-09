package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
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
    private static final String METHOD_VOUCHER_CODE = "VOUCHERCODE";
    private static final String METHOD_CASH_ON_DELIVERY = "CASHONDELIVERY";
    private static final int VOUCHER_LENGTH = 16;
    private static final int VOUCHER_DIGIT_COUNT = 8;
    private static final String VOUCHER_PREFIX = "ESHOP";
    private static final String PAYMENT_DATA_VOUCHER_CODE = "voucherCode";
    private static final String PAYMENT_DATA_ADDRESS = "address";
    private static final String PAYMENT_DATA_DELIVERY_FEE = "deliveryFee";
    private static final Pattern NON_LETTER_PATTERN = Pattern.compile("[^A-Za-z]");

    @Autowired
    private PaymentRepository paymentRepository;
    private final Map<String, Order> orderByPaymentId = new HashMap<>();

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
        String normalizedMethod = normalizeMethod(method);
        switch (normalizedMethod) {
            case METHOD_VOUCHER_CODE:
                return isVoucherValid(paymentData) ? STATUS_SUCCESS : STATUS_REJECTED;
            case METHOD_CASH_ON_DELIVERY:
                return isCashOnDeliveryValid(paymentData) ? STATUS_SUCCESS : STATUS_REJECTED;
            default:
                throw new IllegalArgumentException();
        }
    }

    private String normalizeMethod(String method) {
        if (method == null) {
            throw new IllegalArgumentException();
        }
        return NON_LETTER_PATTERN.matcher(method).replaceAll("").toUpperCase();
    }

    private boolean isVoucherValid(Map<String, String> paymentData) {
        String voucherCode = getPaymentDataValue(paymentData, PAYMENT_DATA_VOUCHER_CODE);
        if (voucherCode == null) {
            return false;
        }

        if (!hasValidVoucherFormat(voucherCode)) {
            return false;
        }

        return countDigits(voucherCode) == VOUCHER_DIGIT_COUNT;
    }

    private boolean isCashOnDeliveryValid(Map<String, String> paymentData) {
        String address = getPaymentDataValue(paymentData, PAYMENT_DATA_ADDRESS);
        String deliveryFee = getPaymentDataValue(paymentData, PAYMENT_DATA_DELIVERY_FEE);
        return isNotBlank(address) && isNotBlank(deliveryFee);
    }

    private boolean hasValidVoucherFormat(String voucherCode) {
        return voucherCode.length() == VOUCHER_LENGTH && voucherCode.startsWith(VOUCHER_PREFIX);
    }

    private String getPaymentDataValue(Map<String, String> paymentData, String key) {
        if (paymentData == null) {
            return null;
        }
        return paymentData.get(key);
    }

    private int countDigits(String value) {
        int digits = 0;
        for (char currentChar : value.toCharArray()) {
            if (Character.isDigit(currentChar)) {
                digits += 1;
            }
        }
        return digits;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
