package org.pom.utils.seleniumutils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.pom.enums.ConfigEnum;

import java.time.Duration;

public class JavaScriptActionUtilities {

    JavascriptExecutor javascriptExecutor;

    protected WebDriver driver;

    public JavaScriptActionUtilities(WebDriver driver) {
        this.driver = driver;
        javascriptExecutor = (JavascriptExecutor) driver;
    }

    public static JavaScriptActionUtilities getInstance(WebDriver driver) {
        return new JavaScriptActionUtilities(driver);
    }

    public void scrollDown() {
        actionUsingJavaScript("scrollDown", null);
    }

    public void scrollUp() {
        actionUsingJavaScript("scrollUp", null);
    }

    public void scrollToBottom() {
        actionUsingJavaScript("scrollToBottom", null);
    }

    public void scrollToTop() {
        actionUsingJavaScript("scrollToTop", null);
    }

    public void scrollToCentre(WebElement element) {
        actionUsingJavaScript("scrollToCentre", element);
    }

    public void scrollToElement(WebElement element) {
        actionUsingJavaScript("scrollToElement", element);
    }

    private void actionUsingJavaScript(String actionName, WebElement element) {
        if (element != null) WaitUtilities.getInstance(driver).waitForElementVisible(element);
        try {
            switch (actionName) {
                case "scrollToElement" -> javascriptExecutor.executeScript("arguments[0].scrollIntoView();", element);
                case "scrollToCentre" -> javascriptExecutor.executeScript(
                        "var element = arguments[0];" +
                                "var rect = element.getBoundingClientRect();" +
                                "window.scrollBy(0, rect.top + window.pageYOffset - (window.innerHeight / 4));",
                        element);
                case "scrollToTop" -> javascriptExecutor.executeScript("window.scrollTo(0, 0)");
                case "scrollToBottom" -> javascriptExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                case "scrollUp" -> javascriptExecutor.executeScript("window.scrollBy(0,-350)", "");
                case "scrollDown" -> javascriptExecutor.executeScript("window.scrollBy(0,350)", "");
                case "clearText" -> javascriptExecutor.executeScript("arguments[0].value = '';", element);
                default -> System.out.println("INFO: Invalid action <" + actionName + "> provided for JavaScript.");
            }
        } catch (Exception e) {
            String stepDesc = "ERROR: Unable to perform <" + actionName + "> operation due to exception - <" + e.getClass().getSimpleName() + ">.";
            System.out.println(stepDesc);
            throw e;
        }
    }

    public String getUserAgent() {
        Object object = javascriptExecutor.executeScript("return navigator.userAgent");
        if (driver != null && object != null)
            return object.toString();
        else
            return "";
    }

    public void scrollModalPopupToBottom(String dialogWindowTagName, WebElement modalContent) {
        try {
            javascriptExecutor.executeScript(
                    "$(arguments[0]).animate({ scrollTop: arguments[1].scrollHeight }, 'fast');",
                    dialogWindowTagName, modalContent);
        } catch (Exception exception) {
            String stepDesc = "ERROR: Unable to perform scroll operation due to exception - <" + exception.getClass().getSimpleName() + ">.";
            System.out.println(stepDesc);
            throw exception;
        }
    }

    public void clearText(WebElement element) {
        actionUsingJavaScript("clearText", element);
    }

    public void waitForPageInReadyState() {
        int timeOut = Integer.parseInt(ConfigEnum.WAIT_TIMEOUT.getValue());
        new WebDriverWait(driver, Duration.ofSeconds(timeOut)).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }
}
