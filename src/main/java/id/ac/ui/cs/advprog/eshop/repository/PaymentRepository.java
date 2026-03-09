package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {
    private final List<Payment> paymentData = new ArrayList<>();

    public Payment save(Payment payment) {
        int existingPaymentIndex = findExistingPaymentIndex(payment.getId());
        if (existingPaymentIndex >= 0) {
            paymentData.set(existingPaymentIndex, payment);
        } else {
            paymentData.add(payment);
        }
        return payment;
    }

    public Payment findById(String paymentId) {
        for (Payment savedPayment : paymentData) {
            if (savedPayment.getId().equals(paymentId)) {
                return savedPayment;
            }
        }
        return null;
    }

    public List<Payment> findAll() {
        return new ArrayList<>(paymentData);
    }

    private int findExistingPaymentIndex(String paymentId) {
        for (int i = 0; i < paymentData.size(); i++) {
            if (paymentData.get(i).getId().equals(paymentId)) {
                return i;
            }
        }
        return -1;
    }
}
