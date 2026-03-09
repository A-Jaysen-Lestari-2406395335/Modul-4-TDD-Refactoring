package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Payment {
    private final String id;
    private final String method;
    private String status;
    private final Map<String, String> paymentData;

    public Payment(String id, String method, String status, Map<String, String> paymentData) {
        this.id = id;
        this.method = method;
        this.status = status;
        this.paymentData = copyPaymentData(paymentData);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Map<String, String> copyPaymentData(Map<String, String> paymentData) {
        if (paymentData == null || paymentData.isEmpty()) {
            return new HashMap<>();
        }
        return new HashMap<>(paymentData);
    }
}
