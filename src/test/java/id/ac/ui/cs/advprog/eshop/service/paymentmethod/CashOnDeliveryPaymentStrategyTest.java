package id.ac.ui.cs.advprog.eshop.service.paymentmethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CashOnDeliveryPaymentStrategyTest {
    private CashOnDeliveryPaymentStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new CashOnDeliveryPaymentStrategy();
    }

    @Test
    void getMethodKeyReturnsCashOnDelivery() {
        assertEquals("CASHONDELIVERY", strategy.getMethodKey());
    }

    @Test
    void determineStatusRejectedWhenDataIsNull() {
        assertEquals("REJECTED", strategy.determineStatus(null));
    }

    @Test
    void determineStatusRejectedWhenAddressBlank() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("address", "");
        paymentData.put("deliveryFee", "10000");

        assertEquals("REJECTED", strategy.determineStatus(paymentData));
    }

    @Test
    void determineStatusRejectedWhenDeliveryFeeNull() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("address", "Depok");
        paymentData.put("deliveryFee", null);

        assertEquals("REJECTED", strategy.determineStatus(paymentData));
    }

    @Test
    void determineStatusSuccessWhenAddressAndFeePresent() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("address", "Depok");
        paymentData.put("deliveryFee", "10000");

        assertEquals("SUCCESS", strategy.determineStatus(paymentData));
    }
}
