package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class PaymentFunctionalTest {
    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String paymentDetailUrl;
    private String paymentAdminListUrl;

    @BeforeEach
    void setUp() {
        paymentDetailUrl = String.format("%s:%d/payment/detail", testBaseUrl, serverPort);
        paymentAdminListUrl = String.format("%s:%d/payment/admin/list", testBaseUrl, serverPort);
    }

    @Test
    void paymentDetailPageTitleIsCorrect(ChromeDriver driver) {
        driver.get(paymentDetailUrl);
        assertEquals("Payment Detail", driver.getTitle());
    }

    @Test
    void paymentDetailByIdIsShown(ChromeDriver driver) {
        String paymentId = createPaymentAndGetId(driver, "order-payment-1");

        driver.get(String.format("%s:%d/payment/detail/%s", testBaseUrl, serverPort, paymentId));

        assertFalse(driver.findElements(By.xpath("//*[contains(text(), '" + paymentId + "')]")).isEmpty());
    }

    @Test
    void paymentAdminListShowsPayment(ChromeDriver driver) {
        String paymentId = createPaymentAndGetId(driver, "order-payment-2");

        driver.get(paymentAdminListUrl);

        assertFalse(driver.findElements(By.xpath("//*[contains(text(), '" + paymentId + "')]")).isEmpty());
    }

    @Test
    void paymentAdminSetStatusToSuccess(ChromeDriver driver) {
        String paymentId = createPaymentAndGetId(driver, "order-payment-3");

        driver.get(String.format("%s:%d/payment/admin/detail/%s", testBaseUrl, serverPort, paymentId));
        driver.findElement(By.id("statusInput")).sendKeys("SUCCESS");
        driver.findElement(By.id("setStatusButton")).click();

        assertFalse(driver.findElements(By.xpath("//*[contains(text(), 'SUCCESS')]")).isEmpty());
    }

    private String createPaymentAndGetId(ChromeDriver driver, String orderId) {
        String createOrderUrl = String.format("%s:%d/order/create", testBaseUrl, serverPort);
        driver.get(createOrderUrl);
        driver.findElement(By.id("orderIdInput")).sendKeys(orderId);
        driver.findElement(By.id("authorInput")).sendKeys("Admin");
        driver.findElement(By.id("productNameInput")).sendKeys("Mouse");
        driver.findElement(By.id("productQuantityInput")).sendKeys("1");
        driver.findElement(By.tagName("button")).click();

        driver.findElement(By.id("methodInput")).sendKeys("Cash on Delivery");
        driver.findElement(By.id("addressInput")).sendKeys("Depok");
        driver.findElement(By.id("deliveryFeeInput")).sendKeys("10000");
        driver.findElement(By.id("payButton")).click();

        WebElement paymentIdLabel = driver.findElement(By.xpath("//*[contains(text(), 'Payment ID:')]"));
        String labelText = paymentIdLabel.getText();
        return labelText.replace("Payment ID:", "").trim();
    }
}
