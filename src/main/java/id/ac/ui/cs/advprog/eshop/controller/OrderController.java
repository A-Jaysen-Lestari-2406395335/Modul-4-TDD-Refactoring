package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create")
    public String createOrderPage() {
        return "orderCreate";
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam String orderId,
                              @RequestParam String author,
                              @RequestParam String productName,
                              @RequestParam int productQuantity) {
        Product product = new Product();
        product.setProductId(orderId + "-product");
        product.setProductName(productName);
        product.setProductQuantity(productQuantity);

        List<Product> products = new ArrayList<>();
        products.add(product);

        Order order = new Order(orderId, products, System.currentTimeMillis(), author);
        orderService.createOrder(order);
        return "redirect:/order/pay/" + orderId;
    }

    @GetMapping("/history")
    public String orderHistoryPage() {
        return "orderHistory";
    }

    @PostMapping("/history")
    public String orderHistory(@RequestParam String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("orders", orders);
        return "orderHistoryList";
    }

    @GetMapping("/pay/{orderId}")
    public String payOrderPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        model.addAttribute("order", order);
        return "orderPay";
    }

    @PostMapping("/pay/{orderId}")
    public String payOrder(@PathVariable String orderId,
                           @RequestParam String method,
                           @RequestParam(required = false) String voucherCode,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) String deliveryFee,
                           Model model) {
        Order order = orderService.findById(orderId);
        Map<String, String> paymentData = new HashMap<>();
        if (voucherCode != null) {
            paymentData.put("voucherCode", voucherCode);
        }
        if (address != null) {
            paymentData.put("address", address);
        }
        if (deliveryFee != null) {
            paymentData.put("deliveryFee", deliveryFee);
        }

        Payment payment = paymentService.addPayment(order, method, paymentData);
        model.addAttribute("payment", payment);
        return "orderPayResult";
    }
}
