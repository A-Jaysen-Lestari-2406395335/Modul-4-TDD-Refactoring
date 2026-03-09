package id.ac.ui.cs.advprog.eshop.service.paymentmethod;

import java.util.Map;

public interface PaymentMethodStrategy {
    String getMethodKey();

    String determineStatus(Map<String, String> paymentData);
}
