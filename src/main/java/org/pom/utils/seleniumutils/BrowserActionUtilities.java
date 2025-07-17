package org.pom.utils.seleniumutils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;

public class BrowserActionUtilities {

    private final WebDriver driver;

    public BrowserActionUtilities(WebDriver driver) {
        this.driver = driver;
    }
    public static BrowserActionUtilities getInstance(WebDriver driver) {
        return new BrowserActionUtilities(driver);
    }

    public void navigateTo(String url) {
        driver.navigate().to(url);
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    public void back() {
        driver.navigate().back();
    }

    public void forward() {
        driver.navigate().forward();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public void close() {
        driver.close();
    }

    public void quit() {
        driver.quit();
    }

    public void maximize() {
        driver.manage().window().maximize();
    }

    public void minimize() {
        driver.manage().window().minimize();
    }

    public int getOpenTabCount() {
        return driver.getWindowHandles().size();
    }

    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public boolean openNewTab(int index) {
        try {
            ((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(Math.min(index, tabs.size() - 1)));
            return true;
        } catch (Exception e) {
            throw new NoSuchWindowException("Failed to open new tab at index " + index, e);
        }
    }

    public boolean openNewTab() {
        return openNewTab(1);
    }

    public boolean focusOnTab(int index) {
        try {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(index));
            return true;
        } catch (Exception e) {
            throw new NoSuchWindowException("Failed to switch to tab at index " + index, e);
        }
    }

    public boolean focusOnTab() {
        return focusOnTab(0);
    }

    public int getWindowCount() {
        return driver.getWindowHandles().size();
    }
}
