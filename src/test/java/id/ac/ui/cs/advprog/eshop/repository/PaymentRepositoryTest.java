package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentRepositoryTest {
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();
    }

    @Test
    void testSaveCreatePayment() {
        Payment payment = new Payment("payment-1", "Voucher Code", "SUCCESS", new HashMap<>());

        Payment result = paymentRepository.save(payment);

        assertEquals("payment-1", result.getId());
        assertEquals("payment-1", paymentRepository.findById("payment-1").getId());
    }

    @Test
    void testSaveUpdatePayment() {
        Payment payment = new Payment("payment-1", "Voucher Code", "REJECTED", new HashMap<>());
        paymentRepository.save(payment);

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment updated = new Payment("payment-1", "Voucher Code", "SUCCESS", paymentData);

        paymentRepository.save(updated);

        Payment found = paymentRepository.findById("payment-1");
        assertEquals("SUCCESS", found.getStatus());
        assertEquals("ESHOP1234ABC5678", found.getPaymentData().get("voucherCode"));
    }

    @Test
    void testFindByIdIfNotFound() {
        assertNull(paymentRepository.findById("not-found"));
    }

    @Test
    void testFindAll() {
        paymentRepository.save(new Payment("payment-1", "Voucher Code", "SUCCESS", new HashMap<>()));
        paymentRepository.save(new Payment("payment-2", "Cash on Delivery", "REJECTED", new HashMap<>()));

        List<Payment> payments = paymentRepository.findAll();

        assertEquals(2, payments.size());
    }

    @Test
    void testSaveCreatePaymentWhenAnotherPaymentAlreadyExists() {
        paymentRepository.save(new Payment("payment-1", "Voucher Code", "SUCCESS", new HashMap<>()));
        Payment payment2 = new Payment("payment-2", "Cash on Delivery", "REJECTED", new HashMap<>());

        Payment result = paymentRepository.save(payment2);

        assertEquals("payment-2", result.getId());
        assertEquals("payment-2", paymentRepository.findById("payment-2").getId());
    }
}
