package id.ac.ui.cs.advprog.eshop.service.paymentmethod;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CashOnDeliveryPaymentStrategy implements PaymentMethodStrategy {
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String METHOD_KEY = "CASHONDELIVERY";
    private static final String PAYMENT_DATA_ADDRESS = "address";
    private static final String PAYMENT_DATA_DELIVERY_FEE = "deliveryFee";

    @Override
    public String getMethodKey() {
        return METHOD_KEY;
    }

    @Override
    public String determineStatus(Map<String, String> paymentData) {
        String address = getPaymentDataValue(paymentData, PAYMENT_DATA_ADDRESS);
        String deliveryFee = getPaymentDataValue(paymentData, PAYMENT_DATA_DELIVERY_FEE);
        return isNotBlank(address) && isNotBlank(deliveryFee) ? STATUS_SUCCESS : STATUS_REJECTED;
    }

    private String getPaymentDataValue(Map<String, String> paymentData, String key) {
        if (paymentData == null) {
            return null;
        }
        return paymentData.get(key);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
