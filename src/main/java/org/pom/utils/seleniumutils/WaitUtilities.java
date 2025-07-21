package org.pom.utils.seleniumutils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.pom.enums.ConfigEnum;
import org.pom.enums.LogLevelEnum;
import org.pom.listeners.ExtentReportListeners;
import org.pom.utils.StringHelper;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class WaitUtilities {
    private static final String WAIT_FOR_ELEMENT_CLICKABLE_TEXT = "waitForElementClickable";
    private static final String WAIT_FOR_ELEMENT_VISIBLE_TEXT = "waitForElementVisible";
    private static final String CLOSE_IB_TAGE = "</i></b>";
    private static final String DUE_TO_EXCEPTION_TAG_WITHIBTAG = "</b> due to exception - <b><i>";
    int timeOut = Integer.parseInt(ConfigEnum.WAIT_TIMEOUT.getValue());
    WebDriverWait wait;
    WebDriverWait customWait;
    JavascriptExecutor javascriptExecutor;

    protected WebDriver driver;
    FluentWait<WebDriver> fluentWait;

    public WaitUtilities(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        javascriptExecutor = (JavascriptExecutor) driver;
    }

    private WaitUtilities() {
    }

    public static WaitUtilities getInstance(WebDriver driver) {
        if (driver == null) return new WaitUtilities();
        else return new WaitUtilities(driver);
    }

    // ****************************** Wait Events ******************************//

    //@Overloading fnWaitForElementClickable
    public WebElement waitForElementClickable(WebElement element, String elementName, int customTimeOut) {
        return waitForCondition(WAIT_FOR_ELEMENT_CLICKABLE_TEXT, element, elementName, customTimeOut, LogLevelEnum.FAIL);
    }

    public WebElement waitForElementClickable(WebElement element, String elementName) {
        return waitForElementClickable(element, elementName, timeOut);
    }

    public WebElement waitForElementClickable(WebElement element, String elementName, LogLevelEnum logLevelEnum) {
        return waitForCondition(WAIT_FOR_ELEMENT_CLICKABLE_TEXT, element, elementName, timeOut, logLevelEnum);
    }

    public WebElement waitForElementClickable(WebElement element, int customTimeOut) {
        return waitForElementClickable(element, null, customTimeOut);
    }

    public WebElement waitForElementClickable(WebElement element) {
        return waitForElementClickable(element, null, timeOut);
    }

    public WebElement waitForElementVisible(WebElement element, String elementName, int customTimeOut) {
        return waitForCondition(WAIT_FOR_ELEMENT_VISIBLE_TEXT, element, elementName, customTimeOut, LogLevelEnum.FAIL);
    }

    public WebElement waitForElementVisible(WebElement element, String elementName, LogLevelEnum logLevelEnum) {
        return waitForCondition(WAIT_FOR_ELEMENT_VISIBLE_TEXT, element, elementName, timeOut, logLevelEnum);
    }

    public WebElement waitForElementVisible(WebElement element, String elementName) {
        return waitForElementVisible(element, elementName, timeOut);
    }

    public WebElement waitForElementVisible(WebElement element, int customTimeOut) {
        return waitForElementVisible(element, null, customTimeOut);
    }

    public WebElement waitForElementVisible(WebElement element) {
        return waitForElementVisible(element, null, timeOut);
    }

    private WebElement waitForCondition(String condition, WebElement element, String elementName, int customTimeOut, LogLevelEnum logLevelEnum) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(customTimeOut));
        WebElement returnElement = null;
        try {
            switch (condition) {
                case WAIT_FOR_ELEMENT_VISIBLE_TEXT ->
                        returnElement = wait.until(ExpectedConditions.visibilityOf(element));
                case WAIT_FOR_ELEMENT_CLICKABLE_TEXT ->
                        returnElement = wait.until(ExpectedConditions.elementToBeClickable(element));
                default ->
                        ExtentReportListeners.logStepWithScreenshot("Invalid condition <b>" + condition + "</b> provided.", driver);
            }
        } catch (Exception e) {
            String stepDescription = "";
            if (elementName == null)
                stepDescription = condition + " failed due to exception - <b><i>" + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            else {
                stepDescription = condition + " failed for element <b>" + StringHelper.getInstance().removeScriptTagsFromString(elementName) + DUE_TO_EXCEPTION_TAG_WITHIBTAG + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            }
            ExtentReportListeners.logStepWithScreenshot(stepDescription, driver);
            throw e;
        }
        return returnElement;
    }

    public boolean waitForElementInvisible(WebElement element, String elementName, int customTimeOut) {
        boolean result;
        customWait = new WebDriverWait(driver, Duration.ofSeconds(customTimeOut));
        try {
            result = customWait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            String stepDescription;
            if (elementName == null)
                stepDescription = "waitForElementInvisible failed due to exception - <b><i>" + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            else
                stepDescription = "waitForElementInvisible failed for element <b>" + StringHelper.getInstance().removeScriptTagsFromString(elementName) + DUE_TO_EXCEPTION_TAG_WITHIBTAG + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            ExtentReportListeners.logStepWithScreenshot(stepDescription, driver);
            throw e;
        }
        return result;
    }

    public boolean waitForElementInvisible(WebElement element, String elementName) {
        return waitForElementInvisible(element, elementName, timeOut);
    }

    public boolean waitForElementInvisible(WebElement element, int customTimeOut) {
        return waitForElementInvisible(element, null, customTimeOut);
    }

    public boolean waitForElementInvisible(WebElement element) {
        return waitForElementInvisible(element, null, timeOut);
    }

    public void applyStaticWait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ExtentReportListeners.logStepWithScreenshot(e.getMessage(), driver);
        }
    }

    public void waitForPageReadyStateComplete() {
        try {
            wait.until((ExpectedCondition<Boolean>) driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("Page ready state complete failed due to exception - <b><i>" + e.getClass().getSimpleName() + CLOSE_IB_TAGE, driver);
        }
    }

    public void waitForAjax() {
        try {
            if (Boolean.TRUE.equals(javascriptExecutor.executeScript("return window.jQuery != undefined"))) {
                wait.until((ExpectedCondition<Boolean>) driver1 -> ((JavascriptExecutor) driver1).executeScript("return jQuery.active == 0").equals(true));
            }
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("waitForAjax failed due to exception - <b><i>" + e.getClass().getSimpleName() + CLOSE_IB_TAGE, driver);
        }
    }

    public void waitForPageLoad() {
        waitForPageReadyStateComplete();
        waitForAjax();
    }

    private WebElement waitForLocatorToBeVisible(String locator, String path, String elementName, int customTimeOut) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(customTimeOut));
        WebElement returnElement = null;
        try {
            switch (locator) {
                case "xpath" ->
                        returnElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(path)));
                case "css" ->
                        returnElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(path)));
                case "id" -> returnElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(path)));
                default ->
                        ExtentReportListeners.logStepWithScreenshot("Invalid locator type <b>" + locator + "</b> provided.", driver);
            }
        } catch (Exception e) {
            String stepDescription = "";
            if (elementName == null)
                stepDescription = "visibilityOfElementLocated failed for locator <b>" + path + DUE_TO_EXCEPTION_TAG_WITHIBTAG + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            else
                stepDescription = "visibilityOfElementLocated failed for element <b>" + StringHelper.getInstance().removeScriptTagsFromString(elementName) + DUE_TO_EXCEPTION_TAG_WITHIBTAG + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            ExtentReportListeners.logStepWithScreenshot(stepDescription, driver);
            throw e;
        }
        return returnElement;
    }

    public WebElement waitForLocatorToBeVisible(String locator, String path) {
        return waitForLocatorToBeVisible(locator, path, null, timeOut);
    }

    public WebElement waitForElementVisibleWithPageRefresh(String locator, String path, String elementName, int iterationTimeOut, int iterationValue) {
        WebElement returnElement = null;
        String exceptionMessage = "";
        wait = new WebDriverWait(driver, Duration.ofSeconds(iterationTimeOut));
        for (int i = 0; i < iterationValue; i++) {
            waitForPageLoad();
            try {
                switch (locator) {
                    case "xpath" ->
                            returnElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(path)));
                    case "css" ->
                            returnElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(path)));
                    default -> returnElement = null;
                }
            } catch (Exception e) {
                exceptionMessage = e.getClass().getSimpleName();
            }
            if (returnElement == null) {
                BrowserActionUtilities.getInstance(driver).refresh();
            } else {
                break;
            }
        }
        if (returnElement == null) {
            String stepDescription = "";
            if (elementName == null)
                stepDescription = "waitForElementVisibleWithPageRefresh failed due to exception - <b><i>" + exceptionMessage + CLOSE_IB_TAGE;
            else
                stepDescription = "waitForElementVisibleWithPageRefresh failed for element <b>" + StringHelper.getInstance().removeScriptTagsFromString(elementName) + DUE_TO_EXCEPTION_TAG_WITHIBTAG + exceptionMessage + CLOSE_IB_TAGE;
            ExtentReportListeners.logStepWithScreenshot(stepDescription, driver);
            throw new StaleElementReferenceException(exceptionMessage);
        }
        return returnElement;
    }

    public WebElement waitForElementVisibleWithPageRefresh(WebElement element, String elementName, int iterationTimeOut, int iterationValue) {
        WebElement returnElement = null;
        String exceptionMessage = "";
        for (int i = 0; i < iterationValue; i++) {
            waitForPageLoad();
            try {
                returnElement = waitForElementVisible(element, elementName, iterationTimeOut);
            } catch (Exception e) {
                exceptionMessage = e.getClass().getSimpleName();
            }
            if (returnElement == null) {
                BrowserActionUtilities.getInstance(driver).refresh();
            } else {
                break;
            }
        }
        if (returnElement == null) {
            String stepDescription = "";
            if (elementName == null)
                stepDescription = "waitForElementVisibleWithPageRefresh failed due to exception - <b><i>" + exceptionMessage + CLOSE_IB_TAGE;
            else
                stepDescription = "waitForElementVisibleWithPageRefresh failed for element <b>" + StringHelper.getInstance().removeScriptTagsFromString(elementName) + DUE_TO_EXCEPTION_TAG_WITHIBTAG + exceptionMessage + CLOSE_IB_TAGE;
            ExtentReportListeners.logStepWithScreenshot(stepDescription, driver);
            throw new IllegalArgumentException(exceptionMessage);
        }
        return returnElement;
    }

    public void waitForElementInvisible(List<WebElement> element, String elementName) {
        try {
            wait.until(ExpectedConditions.invisibilityOfAllElements(element));
        } catch (Exception e) {
            String stepDescription;
            if (elementName == null)
                stepDescription = "waitForElementInvisible failed due to exception - <b><i>" + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            else
                stepDescription = "waitForElementInvisible failed for element <b>" + StringHelper.getInstance().removeScriptTagsFromString(elementName) + DUE_TO_EXCEPTION_TAG_WITHIBTAG + e.getClass().getSimpleName() + CLOSE_IB_TAGE;
            ExtentReportListeners.logStepWithScreenshot(stepDescription, driver);
            throw e;
        }
    }

    public void waitForFileDirectoryToBePresent(String path) {
        final int[] seconds = {0};
        try {
            File file = new File(path);
            fluentWait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeOut))
                    .pollingEvery(Duration.ofSeconds(1))
                    .ignoring(TimeoutException.class);
            fluentWait.until(x -> {
                seconds[0] = seconds[0] + 1;
                return file.exists();
            });
        } catch (Exception e) {
            ExtentReportListeners.logStepWithScreenshot("File [" + path + "] not found till " + seconds[0] + " seconds due to " + e.getClass().getSimpleName(), driver);
            throw e;
        }
    }

    public void fluentWaitForElementVisibility(WebElement webElement, int customTimeOut) {
        fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(customTimeOut))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(Exception.class);
        fluentWait.until(ExpectedConditions.visibilityOf(webElement));
    }

    public void fluentWaitForElementInVisibility(WebElement webElement, int customTimeOut) {
        fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(customTimeOut))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(Exception.class);
        fluentWait.until(ExpectedConditions.invisibilityOf(webElement));
    }

    public void fluentWaitForElementVisibility(WebElement webElement){
        fluentWaitForElementVisibility(webElement,timeOut);
    }
}