package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {
    @Mock
    private PaymentService paymentService;

    private PaymentController controller;

    @BeforeEach
    void setUp() {
        controller = new PaymentController();
        ReflectionTestUtils.setField(controller, "paymentService", paymentService);
    }

    @Test
    void paymentDetailPageReturnsTemplate() {
        String viewName = controller.paymentDetailPage();

        assertEquals("paymentDetail", viewName);
    }

    @Test
    void paymentDetailByIdAddsModelAttributes() {
        Payment payment = new Payment("payment-1", "Voucher Code", "SUCCESS", new HashMap<>());
        when(paymentService.getPayment("payment-1")).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String viewName = controller.paymentDetailById("payment-1", model);

        verify(paymentService).getPayment("payment-1");
        assertEquals("paymentDetail", viewName);
        assertEquals(payment, model.getAttribute("payment"));
        assertEquals("payment-1", model.getAttribute("paymentId"));
    }

    @Test
    void paymentAdminListPageAddsPaymentsToModel() {
        List<Payment> payments = List.of(
                new Payment("payment-1", "Voucher Code", "SUCCESS", new HashMap<>()),
                new Payment("payment-2", "Cash on Delivery", "REJECTED", new HashMap<>())
        );
        when(paymentService.getAllPayments()).thenReturn(payments);
        Model model = new ExtendedModelMap();

        String viewName = controller.paymentAdminListPage(model);

        verify(paymentService).getAllPayments();
        assertEquals("paymentAdminList", viewName);
        assertEquals(payments, model.getAttribute("payments"));
    }

    @Test
    void paymentAdminDetailPageAddsPaymentAndId() {
        Payment payment = new Payment("payment-3", "Voucher Code", "SUCCESS", new HashMap<>());
        when(paymentService.getPayment("payment-3")).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String viewName = controller.paymentAdminDetailPage("payment-3", model);

        verify(paymentService).getPayment("payment-3");
        assertEquals("paymentAdminDetail", viewName);
        assertEquals(payment, model.getAttribute("payment"));
        assertEquals("payment-3", model.getAttribute("paymentId"));
    }

    @Test
    void paymentAdminSetStatusUpdatesWhenPaymentExists() {
        Payment payment = new Payment("payment-4", "Voucher Code", "REJECTED", new HashMap<>());
        Payment updated = new Payment("payment-4", "Voucher Code", "SUCCESS", new HashMap<>());
        when(paymentService.getPayment("payment-4")).thenReturn(payment);
        when(paymentService.setStatus(payment, "SUCCESS")).thenReturn(updated);
        Model model = new ExtendedModelMap();

        String viewName = controller.paymentAdminSetStatus("payment-4", "SUCCESS", model);

        verify(paymentService).setStatus(payment, "SUCCESS");
        assertEquals("paymentAdminDetail", viewName);
        assertEquals(updated, model.getAttribute("payment"));
        assertEquals("payment-4", model.getAttribute("paymentId"));
    }

    @Test
    void paymentAdminSetStatusSkipsUpdateWhenPaymentNotFound() {
        when(paymentService.getPayment("payment-5")).thenReturn(null);
        Model model = new ExtendedModelMap();

        String viewName = controller.paymentAdminSetStatus("payment-5", "SUCCESS", model);

        verify(paymentService).getPayment("payment-5");
        verify(paymentService, never()).setStatus(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString());
        assertEquals("paymentAdminDetail", viewName);
        assertNull(model.getAttribute("payment"));
        assertEquals("payment-5", model.getAttribute("paymentId"));
    }
}
