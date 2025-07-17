package org.pom.utils.seleniumutils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;

public class AlertUtils {

    protected WebDriver driver;

    public AlertUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static AlertUtils getInstance(WebDriver driver) {
        return new AlertUtils(driver);
    }

    public void switchToAlert() {
        performAlertAction("switch");
    }

    public void acceptFromAlert() {
        performAlertAction("accept");
    }

    public void dismissFromAlert() {
        performAlertAction("dismiss");
    }

    private void performAlertAction(String action) {
        try {
            Alert alert = driver.switchTo().alert();
            switch (action.toLowerCase()) {
                case "switch" -> {
                    System.out.println("✅ Switched to alert successfully.");
                }
                case "accept" -> {
                    alert.accept();
                    System.out.println("✅ Accepted the alert successfully.");
                }
                case "dismiss" -> {
                    alert.dismiss();
                    System.out.println("✅ Dismissed the alert successfully.");
                }
                default -> {
                    System.out.println("❌ Invalid alert action provided: " + action);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to perform '" + action + "' on alert due to: " + e.getClass().getSimpleName());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
