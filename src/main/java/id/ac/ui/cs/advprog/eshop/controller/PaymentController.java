package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/detail")
    @ResponseBody
    public String paymentDetailPage() {
        return htmlPage("Payment Detail", "<h3>Payment Detail</h3>");
    }

    @GetMapping("/detail/{paymentId}")
    @ResponseBody
    public String paymentDetailById(@PathVariable String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return htmlPage("Payment Detail", "<h3>Payment not found</h3>");
        }

        String content = "<h3>Payment Detail</h3>" +
                "<p>Payment ID: " + payment.getId() + "</p>" +
                "<p>Method: " + payment.getMethod() + "</p>" +
                "<p>Status: " + payment.getStatus() + "</p>";
        return htmlPage("Payment Detail", content);
    }

    @GetMapping("/admin/list")
    @ResponseBody
    public String paymentAdminListPage() {
        List<Payment> payments = paymentService.getAllPayments();
        StringBuilder rows = new StringBuilder();

        for (Payment payment : payments) {
            rows.append("<tr>")
                    .append("<td>").append(payment.getId()).append("</td>")
                    .append("<td>").append(payment.getMethod()).append("</td>")
                    .append("<td>").append(payment.getStatus()).append("</td>")
                    .append("<td><a href=\"/payment/admin/detail/")
                    .append(payment.getId())
                    .append("\">Detail</a></td>")
                    .append("</tr>");
        }

        String content = "<h3>Payment Admin List</h3>" +
                "<table border=\"1\"><thead><tr><th>Payment ID</th><th>Method</th><th>Status</th><th>Action</th></tr></thead>" +
                "<tbody>" + rows + "</tbody></table>";
        return htmlPage("Payment Admin List", content);
    }

    @GetMapping("/admin/detail/{paymentId}")
    @ResponseBody
    public String paymentAdminDetailPage(@PathVariable String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return htmlPage("Payment Admin Detail", "<h3>Payment not found</h3>");
        }

        String content = "<h3>Payment Admin Detail</h3>" +
                "<p>Payment ID: " + payment.getId() + "</p>" +
                "<p>Status: " + payment.getStatus() + "</p>" +
                "<form action=\"/payment/admin/set-status/" + payment.getId() + "\" method=\"post\">" +
                "<label for=\"statusInput\">Status</label>" +
                "<input id=\"statusInput\" name=\"status\" type=\"text\" />" +
                "<button id=\"setStatusButton\" type=\"submit\">Set Status</button>" +
                "</form>";
        return htmlPage("Payment Admin Detail", content);
    }

    @PostMapping("/admin/set-status/{paymentId}")
    @ResponseBody
    public String paymentAdminSetStatus(@PathVariable String paymentId, @RequestParam String status) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return htmlPage("Payment Admin Detail", "<h3>Payment not found</h3>");
        }

        Payment updatedPayment = paymentService.setStatus(payment, status);
        String content = "<h3>Payment Admin Detail</h3>" +
                "<p>Payment ID: " + updatedPayment.getId() + "</p>" +
                "<p>Status: " + updatedPayment.getStatus() + "</p>";
        return htmlPage("Payment Admin Detail", content);
    }

    private String htmlPage(String title, String bodyContent) {
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>" +
                title +
                "</title></head><body>" +
                bodyContent +
                "</body></html>";
    }
}
