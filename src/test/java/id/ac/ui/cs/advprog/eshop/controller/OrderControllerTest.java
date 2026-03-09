package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        controller = new OrderController();
        ReflectionTestUtils.setField(controller, "orderService", orderService);
        ReflectionTestUtils.setField(controller, "paymentService", paymentService);
    }

    @Test
    void createOrderPageReturnsCreateTemplate() {
        String viewName = controller.createOrderPage();

        assertEquals("orderCreate", viewName);
    }

    @Test
    void createOrderCallsServiceAndRedirectsToPayPage() {
        String viewName = controller.createOrder("order-1", "Safira", "Keyboard", 2);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderService).createOrder(orderCaptor.capture());
        Order createdOrder = orderCaptor.getValue();
        assertEquals("order-1", createdOrder.getId());
        assertEquals("Safira", createdOrder.getAuthor());
        assertEquals("Keyboard", createdOrder.getProducts().get(0).getProductName());
        assertEquals(2, createdOrder.getProducts().get(0).getProductQuantity());
        assertEquals("order-1-product", createdOrder.getProducts().get(0).getProductId());
        assertEquals("redirect:/order/pay/order-1", viewName);
    }

    @Test
    void orderHistoryPageReturnsHistoryTemplate() {
        String viewName = controller.orderHistoryPage();

        assertEquals("orderHistory", viewName);
    }

    @Test
    void orderHistoryAddsOrdersToModel() {
        List<Order> orders = List.of(createOrder("order-1"));
        when(orderService.findAllByAuthor("Safira")).thenReturn(orders);
        Model model = new ExtendedModelMap();

        String viewName = controller.orderHistory("Safira", model);

        verify(orderService).findAllByAuthor("Safira");
        assertEquals("orderHistoryList", viewName);
        assertEquals(orders, model.getAttribute("orders"));
    }

    @Test
    void payOrderPageAddsOrderToModel() {
        Order order = createOrder("order-2");
        when(orderService.findById("order-2")).thenReturn(order);
        Model model = new ExtendedModelMap();

        String viewName = controller.payOrderPage("order-2", model);

        verify(orderService).findById("order-2");
        assertEquals("orderPay", viewName);
        assertEquals(order, model.getAttribute("order"));
    }

    @Test
    void payOrderAddsAllPaymentDataWhenProvided() {
        Order order = createOrder("order-3");
        Payment payment = new Payment("payment-1", "Cash on Delivery", "SUCCESS", new HashMap<>());
        when(orderService.findById("order-3")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("Cash on Delivery"), any(Map.class))).thenReturn(payment);
        Model model = new ExtendedModelMap();

        String viewName = controller.payOrder("order-3", "Cash on Delivery",
                "ESHOP1234ABC5678", "Depok", "10000", model);

        ArgumentCaptor<Map<String, String>> paymentDataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(paymentService).addPayment(eq(order), eq("Cash on Delivery"), paymentDataCaptor.capture());
        Map<String, String> paymentData = paymentDataCaptor.getValue();
        assertEquals("ESHOP1234ABC5678", paymentData.get("voucherCode"));
        assertEquals("Depok", paymentData.get("address"));
        assertEquals("10000", paymentData.get("deliveryFee"));
        assertEquals("orderPayResult", viewName);
        assertEquals(payment, model.getAttribute("payment"));
    }

    @Test
    void payOrderAllowsEmptyOptionalPaymentData() {
        Order order = createOrder("order-4");
        Payment payment = new Payment("payment-2", "Voucher Code", "REJECTED", new HashMap<>());
        when(orderService.findById("order-4")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq("Voucher Code"), any(Map.class))).thenReturn(payment);
        Model model = new ExtendedModelMap();

        controller.payOrder("order-4", "Voucher Code", null, null, null, model);

        ArgumentCaptor<Map<String, String>> paymentDataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(paymentService).addPayment(eq(order), eq("Voucher Code"), paymentDataCaptor.capture());
        assertTrue(paymentDataCaptor.getValue().isEmpty());
    }

    private Order createOrder(String orderId) {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Mouse");
        product.setProductQuantity(1);
        products.add(product);
        return new Order(orderId, products, 1708560000L, "Safira");
    }
}
