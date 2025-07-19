package org.pom.utils.seleniumutils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.pom.base.BaseTest;
import org.pom.enums.ConfigEnum;
import org.pom.enums.LogLevelEnum;
import org.pom.listeners.ExtentReportListeners;
import org.pom.utils.StringHelper;

import java.util.List;
import java.util.Objects;

public class PageCommonUtils {
    private static final String FROM_BTAG_TEXT = "</b> from <b>";
    private static final String ACTIONS_TEXT = "actions";
    private static final String SUCCESSFULLY_BTAG_TEXT = "</b> successfully.";
    private static final String DUE_TO_EXCEPTION_IBTAG_TEXT = "</b> due to exception - <b><i>";
    private static final String CLOSE_IB_TAG = "</i></b>.";
    private static final String DEFAULT_TEXT = "default";

    protected WebDriver driver;
    Actions actions;
    JavascriptExecutor javascriptExecutor;

    public PageCommonUtils(WebDriver driver) {
        this.driver = driver;
        actions = new Actions(driver);
        javascriptExecutor = (JavascriptExecutor) driver;
    }

    public static PageCommonUtils imageUniqueSrcgetInstance(WebDriver driver) {
        return new PageCommonUtils(driver);
    }

    public static PageCommonUtils getInstance(WebDriver driver) {
        return new PageCommonUtils(driver);
    }

    // *********** Action Events *************

    private void click(WebElement element, String clickedElementName, String clickedFrom, String actionUsing, boolean isReportStep) {
        WaitUtilities.getInstance(driver).waitForElementClickable(element, clickedElementName);
        try {
            switch (actionUsing) {
                case "submit" -> element.submit();
                case ACTIONS_TEXT -> actions.click(element).build().perform();
                case "javaScript" -> javascriptExecutor.executeScript("arguments[0].click();", element);
                default -> element.click();
            }
            if (isReportStep) {
                ExtentReportListeners.logStepWithScreenshot("Clicked on " + clickedElementName + " from " + clickedFrom, driver);
            }
        } catch (ElementClickInterceptedException e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to click on " + clickedElementName + " due to " + e.getClass().getSimpleName(), driver);
            throw new ElementNotInteractableException("Element is not interactable");
        }
    }

    public void click(WebElement element, String clickedElementName, String clickedFrom, boolean isReportStep) {
        click(element, clickedElementName, clickedFrom, DEFAULT_TEXT, isReportStep);
    }

    public void click(WebElement element, String clickedElementName, String clickedFrom) {
        click(element, clickedElementName, clickedFrom, DEFAULT_TEXT, true);
    }

    public void submit(WebElement element, String clickedElementName, String clickedFrom) {
        click(element, clickedElementName, clickedFrom, "submit", true);
    }

    public void clickUsingActionsClass(WebElement element, String clickedElementName, String clickedFrom) {
        click(element, clickedElementName, clickedFrom, ACTIONS_TEXT, true);
    }

    public void clickUsingJavaScript(WebElement element, String clickedElementName, String clickedFrom) {
        click(element, clickedElementName, clickedFrom, "javaScript", true);
    }

    private void sendKeys(WebElement element, String textFieldName, String value, boolean isMasked, String actionUsing) {
        WaitUtilities.getInstance(driver).waitForElementVisible(element, textFieldName);
        try {
            if (actionUsing.equalsIgnoreCase(ACTIONS_TEXT))
                actions.sendKeys(value).build().perform();
            else
                element.sendKeys(value);
            if (isMasked) value = value.replaceAll("\\S", "*");
            if (value.equalsIgnoreCase("     ")) value = "blank value only";
            ExtentReportListeners.logStepWithScreenshot("Entered value in " + textFieldName + ": " + value, driver);
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to enter value in " + textFieldName + " due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
    }

    public void sendKeys(WebElement element, String textFieldName, String value, Boolean isMasked) {
        sendKeys(element, textFieldName, value, isMasked, DEFAULT_TEXT);
    }

    public void sendKeys(WebElement element, String textFieldName, String value) {
        sendKeys(element, textFieldName, value, false);
    }

    public void sendKeysUsingActionsClass(WebElement element, String textFieldName, String value, Boolean isMasked) {
        sendKeys(element, textFieldName, value, isMasked, ACTIONS_TEXT);
    }

    public void sendKeysUsingJavaScript(WebElement element, String textFieldName, String value, Boolean isMasked) {
        sendKeys(element, textFieldName, value, isMasked, "javascript");
    }

    public void clearUsingKeyboardActions(WebElement element, String textFieldName) {
        clearUsingKeyboardActions(element, textFieldName, true);
    }

    public void clearUsingKeyboardActions(WebElement element, String textFieldName, boolean isReportStep) {
        try {
            element.sendKeys(Keys.chord(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE));
            if (isReportStep)
                ExtentReportListeners.logStepWithScreenshot("Cleared (using keyboard) " + textFieldName, driver);
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to clear (keyboard) " + textFieldName + " due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
    }

    public void clear(WebElement element, String textFieldName, boolean isReportStep) {
        WaitUtilities.getInstance(driver).waitForElementVisible(element, textFieldName);
        try {
            element.clear();
            if (!element.getText().trim().equalsIgnoreCase(""))
                JavaScriptActionUtilities.getInstance(driver).clearText(element);
            if (isReportStep)
                ExtentReportListeners.logStepWithScreenshot("Cleared " + textFieldName, driver);
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to clear " + textFieldName + " due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
    }

    public void clear(WebElement element, String textFieldName) {
        clear(element, textFieldName, false);
    }

    private String getText(WebElement element, String fieldName, boolean isReportStep) {
        String text = "";
        WaitUtilities.getInstance(driver).waitForElementVisible(element, fieldName);
        try {
            text = element.getText();
            if (isReportStep)
                ExtentReportListeners.logStepWithScreenshot("Captured text from " + fieldName + ": " + text, driver);
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to capture text from " + fieldName + " due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
        return text;
    }

    public String getText(WebElement element) {
        return getText(element, "", false);
    }

    public String getText(WebElement element, String fieldName) {
        return getText(element, fieldName, true);
    }

    public String getInnerText(WebElement element, String fieldName) {
        String text = "";
        try {
            Object object = javascriptExecutor.executeScript("return arguments[0].innerText;", element);
            if (object != null)
                text = object.toString();
            System.out.println("Captured text <b>" + text + FROM_BTAG_TEXT + fieldName + SUCCESSFULLY_BTAG_TEXT);
        } catch (Exception e) {
            System.out.println("Unable to capture text from <b>" + fieldName + DUE_TO_EXCEPTION_IBTAG_TEXT + e.getClass().getSimpleName() + CLOSE_IB_TAG);
            throw e;
        }
        return text;
    }

    public String getAttributeValue(WebElement element, String attributeName, String elementName) {
        String value = "";
        WaitUtilities.getInstance(driver).waitForElementVisible(element, elementName);
        try {
            value = element.getAttribute(attributeName);
            ExtentReportListeners.logStepWithScreenshot("Captured attribute '" + attributeName + "' from " + elementName + ": " + value, driver);
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to capture attribute '" + attributeName + "' from " + elementName + " due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
        return value;
    }

    public WebElement createWebElementByLocator(String locator, String value) {
        WebElement element = null;
        WaitUtilities.getInstance(driver).waitForLocatorToBeVisible(locator, value);
        try {
            switch (locator.trim().toLowerCase()) {
                case "xpath": {
                    element = driver.findElement(By.xpath(value));
                    break;
                }
                case "css": {
                    element = driver.findElement(By.cssSelector(value));
                    break;
                }
                case "id": {
                    element = driver.findElement(By.id(value));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unsupported locator: " + locator);
                }
            }
            return element;
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to locate element using " + locator + ": " + value + " due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
    }


    public void clearAndSendKeys(WebElement element, String textFieldName, String value) {
        clear(element, textFieldName);
        sendKeys(element, textFieldName, value);
    }

    public void click(String path, String locator, String clickedElementName, String clickedFrom) {
        WebElement e = createWebElementByLocator(locator, path);
        click(e, clickedElementName, clickedFrom, DEFAULT_TEXT, true);
    }

    public int getElementCount(String xpath) {
        int count = 0;
        List<WebElement> list = driver.findElements(By.xpath(xpath));
        count = list.size();
        return count;
    }

    public void clickOnElementFromDropDownOrList(By locator, String value, String clickedElementName, String clickedFrom) {
        List<WebElement> eleList = driver.findElements(locator);
        int size = eleList.size();
        for (int i = 0; i < size; i++) {
            String text = eleList.get(i).getText();
            if (text.equals(value)) {
                click(eleList.get(i), clickedElementName, clickedFrom);
                break;
            }
        }
    }

    public List<WebElement> createWebElementsByLocator(String locator, String value) {
        List<WebElement> elements;
        WaitUtilities.getInstance(driver).waitForLocatorToBeVisible(locator, value);
        try {
            switch (locator.trim().toLowerCase()) {
                default:
                case "xpath": {
                    elements = driver.findElements(By.xpath(value));
                    break;
                }
                case "css": {
                    elements = driver.findElements(By.cssSelector(value));
                    break;
                }
            }
            return elements;
        } catch (Exception e) {
            System.out.println("Unable to find element due to exception - " + e.getClass().getSimpleName() + ".");
            throw e;
        }
    }

    public void clickClearAndSendKeys(WebElement element, String textFieldName, String value, String clickedFrom) {
        click(element, textFieldName, clickedFrom);
        clear(element, textFieldName);
        sendKeys(element, textFieldName, value);
    }

    public void clickOnDropDown(WebElement parentElement, WebElement optionElement, String fieldName, String clickedFrom) {
        click(parentElement, fieldName, clickedFrom);
        WaitUtilities.getInstance(driver).waitForElementClickable(optionElement);
        click(optionElement, fieldName, clickedFrom);
    }

    public WebElement createRelativeWebElementByLocator(WebElement mainElement, By relativeValue, String position) {
        WebElement targetElement = null;
        try {
            switch (position.trim().toLowerCase()) {
                case "to_right_of": {
                    targetElement = driver.findElement(RelativeLocator.with(relativeValue).toRightOf(mainElement));
                    break;
                }
                case "to_left_of": {
                    targetElement = driver.findElement(RelativeLocator.with(relativeValue).toLeftOf(mainElement));
                    break;
                }
                case "below": {
                    targetElement = driver.findElement(RelativeLocator.with(relativeValue).below(mainElement));
                    break;
                }
                case "above": {
                    targetElement = driver.findElement(RelativeLocator.with(relativeValue).above(mainElement));
                    break;
                }
                case "near": {
                    targetElement = driver.findElement(RelativeLocator.with(relativeValue).near(mainElement));
                    break;
                }
                default: {
                    break;
                }
            }
            return targetElement;
        } catch (Exception e) {
            System.out.println("Unable to locate element due to exception - " + e.getClass().getSimpleName() + ".");
            throw e;
        }
    }

    public void moveCursorToRight(WebElement element, String textFieldName) {
        moveCursorToRight(element, textFieldName, true);
    }

    public void moveCursorToRight(WebElement element, String textFieldName, boolean isReportStep) {
        try {
            element.sendKeys(Keys.END);
            if (isReportStep)
                ExtentReportListeners.logStepWithScreenshot("Moved cursor to end of " + textFieldName, driver);
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Failed to move cursor in " + textFieldName + " due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
    }
}