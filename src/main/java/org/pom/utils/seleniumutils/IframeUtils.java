package org.pom.utils.seleniumutils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class IframeUtils {

    protected WebDriver driver;

    public IframeUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static IframeUtils getInstance(WebDriver driver) {
        return new IframeUtils(driver);
    }

    public void switchToFrame(WebElement element) {
        actionOnIframe("switchTo().frame()", element, 0, "");
    }

    public void switchToDefault() {
        actionOnIframe("switchTo().defaultContent()", null, 0, "");
    }

    public void switchToFrame(int index) {
        actionOnIframe("switchTo().frame(index)", null, index, "");
    }

    public void switchToFrame(String iframeId) {
        actionOnIframe("switchTo().frame(iframeId)", null, 0, iframeId);
    }

    private void actionOnIframe(String actionName, WebElement element, int index, String iframeId) {
        if (element != null) {
            WaitUtilities.getInstance(driver).waitForElementVisible(element);
        }

        try {
            switch (actionName) {
                case "switchTo().defaultContent()" -> {
                    driver.switchTo().defaultContent();
                    System.out.println("✅ Switched to default content successfully.");
                }
                case "switchTo().frame()" -> {
                    driver.switchTo().frame(element);
                    System.out.println("✅ Switched to iframe using WebElement.");
                }
                case "switchTo().frame(index)" -> {
                    driver.switchTo().frame(index);
                    System.out.println("✅ Switched to iframe at index: " + index);
                }
                case "switchTo().frame(iframeId)" -> {
                    driver.switchTo().frame(iframeId);
                    System.out.println("✅ Switched to iframe with ID/name: " + iframeId);
                }
                default -> {
                    System.out.println("❌ Invalid iframe action: " + actionName);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to perform iframe action '" + actionName + "' due to: " + e.getClass().getSimpleName());
            throw e;
        }
    }
}
