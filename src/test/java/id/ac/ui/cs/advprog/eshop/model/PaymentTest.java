package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentTest {

    @Test
    void testCreatePayment() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        Payment payment = new Payment("payment-1", "Voucher Code", "SUCCESS", paymentData);

        assertEquals("payment-1", payment.getId());
        assertEquals("Voucher Code", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("ESHOP1234ABC5678", payment.getPaymentData().get("voucherCode"));
    }

    @Test
    void testCreatePaymentWithNullPaymentData() {
        Payment payment = new Payment("payment-1", "Voucher Code", "SUCCESS", null);

        assertTrue(payment.getPaymentData().isEmpty());
    }

    @Test
    void testSetStatus() {
        Payment payment = new Payment("payment-1", "Voucher Code", "REJECTED", new HashMap<>());

        payment.setStatus("SUCCESS");

        assertEquals("SUCCESS", payment.getStatus());
    }
}
