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
class OrderFunctionalTest {
    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String createUrl;
    private String historyUrl;

    @BeforeEach
    void setUp() {
        createUrl = String.format("%s:%d/order/create", testBaseUrl, serverPort);
        historyUrl = String.format("%s:%d/order/history", testBaseUrl, serverPort);
    }

    @Test
    void createPageTitleIsCorrect(ChromeDriver driver) {
        driver.get(createUrl);
        assertEquals("Create New Order", driver.getTitle());
    }

    @Test
    void createOrderAndRedirectToPayPage(ChromeDriver driver) {
        driver.get(createUrl);

        driver.findElement(By.id("orderIdInput")).sendKeys("order-1");
        driver.findElement(By.id("authorInput")).sendKeys("Bambang");
        driver.findElement(By.id("productNameInput")).sendKeys("Keyboard");
        driver.findElement(By.id("productQuantityInput")).sendKeys("2");
        driver.findElement(By.tagName("button")).click();

        assertEquals(String.format("%s:%d/order/pay/order-1", testBaseUrl, serverPort), driver.getCurrentUrl());
        assertFalse(driver.findElements(By.xpath("//*[contains(text(), 'Pay Order')]")).isEmpty());
    }

    @Test
    void historyPageTitleIsCorrect(ChromeDriver driver) {
        driver.get(historyUrl);
        assertEquals("Order History", driver.getTitle());
    }

    @Test
    void submitHistoryShouldShowOrders(ChromeDriver driver) {
        createOrder(driver, "order-2", "Safira");

        driver.get(historyUrl);
        driver.findElement(By.id("authorInput")).sendKeys("Safira");
        driver.findElement(By.tagName("button")).click();

        assertEquals(String.format("%s:%d/order/history", testBaseUrl, serverPort), driver.getCurrentUrl());
        assertFalse(driver.findElements(By.xpath("//*[contains(text(), 'order-2')]")).isEmpty());
    }

    @Test
    void submitPaymentShouldShowPaymentIdPage(ChromeDriver driver) {
        createOrder(driver, "order-3", "Udin");

        driver.get(String.format("%s:%d/order/pay/order-3", testBaseUrl, serverPort));
        driver.findElement(By.id("methodInput")).sendKeys("Cash on Delivery");
        driver.findElement(By.id("addressInput")).sendKeys("Depok");
        driver.findElement(By.id("deliveryFeeInput")).sendKeys("10000");
        driver.findElement(By.id("payButton")).click();

        assertFalse(driver.findElements(By.xpath("//*[contains(text(), 'Payment ID')]")).isEmpty());
    }

    private void createOrder(ChromeDriver driver, String orderId, String author) {
        driver.get(createUrl);

        WebElement orderIdInput = driver.findElement(By.id("orderIdInput"));
        orderIdInput.sendKeys(orderId);

        WebElement authorInput = driver.findElement(By.id("authorInput"));
        authorInput.sendKeys(author);

        driver.findElement(By.id("productNameInput")).sendKeys("Mouse");
        driver.findElement(By.id("productQuantityInput")).sendKeys("1");
        driver.findElement(By.tagName("button")).click();
    }
}
