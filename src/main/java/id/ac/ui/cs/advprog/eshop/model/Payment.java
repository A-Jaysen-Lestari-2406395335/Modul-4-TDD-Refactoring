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
        this.paymentData = paymentData == null ? new HashMap<>() : new HashMap<>(paymentData);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
