package org.pom.utils.seleniumutils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.pom.enums.ConfigEnum;
import org.pom.enums.LogLevelEnum;
import org.pom.utils.ReportUtils;

public class CaptureConsoleLogUtils {

    protected WebDriver driver;

    public CaptureConsoleLogUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static CaptureConsoleLogUtils getInstance(WebDriver driver) {
        return new CaptureConsoleLogUtils(driver);
    }

    public void captureConsoleLogs() {
        if (ConfigEnum.BROWSER.getValue().equalsIgnoreCase("CHROME")) {
            WaitUtilities.getInstance(driver).applyStaticWait(2);
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : logEntries) {
                ReportUtils.getInstance().reportStepWithoutScreenshot("Console logs are -> " + entry.getLevel() + "-->" + entry.getMessage(), LogLevelEnum.INFO);
            }
        }
    }
}