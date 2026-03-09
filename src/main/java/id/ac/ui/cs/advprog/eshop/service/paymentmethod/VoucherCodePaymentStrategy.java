package id.ac.ui.cs.advprog.eshop.service.paymentmethod;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VoucherCodePaymentStrategy implements PaymentMethodStrategy {
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String METHOD_KEY = "VOUCHERCODE";
    private static final int VOUCHER_LENGTH = 16;
    private static final int VOUCHER_DIGIT_COUNT = 8;
    private static final String VOUCHER_PREFIX = "ESHOP";
    private static final String PAYMENT_DATA_VOUCHER_CODE = "voucherCode";

    @Override
    public String getMethodKey() {
        return METHOD_KEY;
    }

    @Override
    public String determineStatus(Map<String, String> paymentData) {
        String voucherCode = getPaymentDataValue(paymentData, PAYMENT_DATA_VOUCHER_CODE);
        if (voucherCode == null) {
            return STATUS_REJECTED;
        }

        if (!hasValidVoucherFormat(voucherCode)) {
            return STATUS_REJECTED;
        }

        return countDigits(voucherCode) == VOUCHER_DIGIT_COUNT ? STATUS_SUCCESS : STATUS_REJECTED;
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
}
