package id.ac.ui.cs.advprog.eshop.service.paymentmethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VoucherCodePaymentStrategyTest {
    private VoucherCodePaymentStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new VoucherCodePaymentStrategy();
    }

    @Test
    void getMethodKeyReturnsVoucherCode() {
        assertEquals("VOUCHERCODE", strategy.getMethodKey());
    }

    @Test
    void determineStatusRejectedWhenDataIsNull() {
        assertEquals("REJECTED", strategy.determineStatus(null));
    }

    @Test
    void determineStatusRejectedWhenVoucherMissing() {
        assertEquals("REJECTED", strategy.determineStatus(new HashMap<>()));
    }

    @Test
    void determineStatusRejectedWhenFormatInvalid() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "INVALID");

        assertEquals("REJECTED", strategy.determineStatus(paymentData));
    }

    @Test
    void determineStatusRejectedWhenDigitCountIsNotEight() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOPABCDABC1234");

        assertEquals("REJECTED", strategy.determineStatus(paymentData));
    }

    @Test
    void determineStatusRejectedWhenPrefixIsInvalid() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "XSHOP1234ABC5678");

        assertEquals("REJECTED", strategy.determineStatus(paymentData));
    }

    @Test
    void determineStatusSuccessWhenVoucherValid() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        assertEquals("SUCCESS", strategy.determineStatus(paymentData));
    }
}
