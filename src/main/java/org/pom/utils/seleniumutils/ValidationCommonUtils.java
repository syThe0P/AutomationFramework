package org.pom.utils.seleniumutils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.pom.enums.ConfigEnum;
import org.pom.enums.LogLevelEnum;
import org.pom.utils.StringHelper;

public class ValidationCommonUtils {
    protected WebDriver driver;
    private static final String CLOSE_B_TAG = "</b>";
    private static final String IS_NOT_PRESENT_TEXT = " is not present on ";

    public ValidationCommonUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static ValidationCommonUtils getInstance(WebDriver driver) {
        return new ValidationCommonUtils(driver);
    }

    // *********** Boolean Events ******************
    private boolean isDisplayedOrSelectedOrEnabled(WebElement element, String elementName, String pageName, String actionName) {
        boolean result = false;
        WaitUtilities.getInstance(driver).waitForElementVisible(element, elementName);
        elementName = StringHelper.getInstance().removeScriptTagsFromString(elementName);
        try {
            switch (actionName) {
                case "isSelected" -> {
                    result = element.isSelected();
                    System.out.println("Element <b>" + elementName + "</b> on <b>" + pageName + "</b> is successfully " + (result ? "selected" : "not selected") + ".");
                }
                case "isEnabled" -> {
                    result = element.isEnabled();
                    System.out.println("Element <b>" + elementName + "</b> on <b>" + pageName + "</b> is successfully " + (result ? "enabled" : "not enabled") + ".");
                }
                default -> {
                    result = element.isDisplayed();
                    System.out.println("Element <b>" + elementName + "</b> on <b>" + pageName + "</b> is successfully " + (result ? "displayed" : "not displayed") + ".");
                }
            }
        } catch (Exception e) {
            System.out.println("Unable to perform operation <b>" + actionName + "</b> on <b>" + elementName + "</b> from <b>" + pageName + "</b> due to exception - <b><i>" + e.getClass().getSimpleName() + "</i></b>.");
            throw e;
        }
        return result;
    }

    public boolean isElementPresent(WebElement element, String elementName, String pageName) {
        boolean result = false;
        elementName = StringHelper.getInstance().removeScriptTagsFromString(elementName);
        try {
            result = element.isDisplayed();
            System.out.println("Element <b>" + elementName + "</b> on <b>" + pageName + "</b> is successfully " + (result ? "displayed" : "not displayed") + ".");
        } catch (Exception e) {
            System.out.println("Element <b>" + elementName + "</b> is not present on <b>" + pageName + "</b> due to exception - <b><i>" + e.getClass().getSimpleName() + "</i></b>.");
        }
        return result;
    }

    public boolean isElementPresent(String xpath, String elementName, String pageName) {
        try {
            WebElement e = driver.findElement(By.xpath(xpath));
            return isElementPresent(e, elementName, pageName);
        } catch (Exception e) {
            System.out.println("Element <b>" + elementName + "</b> is not present on <b>" + pageName + "</b> due to exception - <b><i>" + e.getClass().getSimpleName() + "</i></b>.");
            return false;
        }
    }

    public boolean isDisplayed(WebElement element, String elementName, String pageName, boolean reportStep) {
        return isDisplayedOrSelectedOrEnabled(element, elementName, pageName, "isDisplayed");
    }

    public boolean isDisplayed(WebElement element, String elementName, String pageName) {
        return isDisplayed(element, elementName, pageName, true);
    }

    public boolean isDisplayed(String value, String locator, String elementName, String pageName) {
        WebElement e = PageCommonUtils.getInstance(driver).createWebElementByLocator(locator, value);
        return isDisplayedOrSelectedOrEnabled(e, elementName, pageName, "isDisplayed");
    }

    public boolean isSelected(WebElement element, String elementName, String pageName, boolean reportStep) {
        return isDisplayedOrSelectedOrEnabled(element, elementName, pageName, "isSelected");
    }

    public boolean isSelected(WebElement element, String elementName, String pageName) {
        return isSelected(element, elementName, pageName, true);
    }

    public boolean isEnabled(WebElement element, String elementName, String pageName, boolean reportStep) {
        return isDisplayedOrSelectedOrEnabled(element, elementName, pageName, "isEnabled");
    }

    public boolean isEnabled(WebElement element, String elementName, String pageName) {
        return isEnabled(element, elementName, pageName, true);
    }
}