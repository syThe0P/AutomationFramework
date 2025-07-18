package org.pom.utils;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.pom.base.BaseTest;
import org.pom.enums.LogLevelEnum;
import org.pom.listeners.ExtentReportListeners;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.io.IOException;

public class ReportUtils extends BaseTest {

    private static int counter = 0;
    public static final int MAX_TRY = 0;
    private static final boolean ALWAYS_TAKE_SCREENSHOT;

    static {
        boolean alwaysScreenshot = false;
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            Properties props = new Properties();
            props.load(fis);
            alwaysScreenshot = Boolean.parseBoolean(props.getProperty("report.screenshot.always", "false"));
        } catch (IOException e) {
            // fallback to false if file not found
        }
        ALWAYS_TAKE_SCREENSHOT = alwaysScreenshot;
    }

    public static synchronized ReportUtils getInstance() {
        return new ReportUtils();
    }

    public synchronized void reportStep(WebDriver driver, String stepDescription, LogLevelEnum logLevelEnum, boolean isTakeScreenshot) {
        boolean takeScreenshot = isTakeScreenshot || ALWAYS_TAKE_SCREENSHOT;
        switch (logLevelEnum) {
            case WARNING -> reportStepWarning(driver, stepDescription, takeScreenshot);
            case SKIP -> reportStepSkip(driver, stepDescription, takeScreenshot);
            case FAIL -> {
                if (getCount() < MAX_TRY) {
                    setCount(getCount() + 1);
                    reportStepWarning(driver, stepDescription, takeScreenshot);
                } else reportStepFail(driver, stepDescription, takeScreenshot);
            }
            case PASS -> reportStepPass(driver, stepDescription, takeScreenshot);
            case INFO -> reportStepInfo(driver, stepDescription, takeScreenshot);
        }
        logger.log(Level.INFO, stepDescription);
    }

    public void reportStep(WebDriver driver, String stepDescription, LogLevelEnum logLevelEnum) {
        reportStep(driver, stepDescription, logLevelEnum, true);
    }

    private void reportStepWithoutScreenshot(WebDriver driver, String stepDescription, LogLevelEnum logLevelEnum, LinkedHashMap<String, String> linkedHashMapTestParameters) {
        reportStep(driver, stepDescription, logLevelEnum, false);
        if (linkedHashMapTestParameters != null) reportStepLog(linkedHashMapTestParameters);
    }

    private void reportStepWithoutScreenshot(WebDriver driver, String stepDescription, LogLevelEnum logLevelEnum) {
        reportStepWithoutScreenshot(driver, stepDescription, logLevelEnum, null);
    }

    public void reportStepWithoutScreenshot(String stepDescription, LogLevelEnum logLevelEnum, LinkedHashMap<String, String> linkedHashMapTestParameters) {
        reportStepWithoutScreenshot(null, stepDescription, logLevelEnum, linkedHashMapTestParameters);
    }

    public void reportStepWithoutScreenshot(String stepDescription, LogLevelEnum logLevelEnum) {
        reportStepWithoutScreenshot(null, stepDescription, logLevelEnum, null);
    }

    private void reportStepPass(WebDriver driver, String stepDescription, boolean isTakeScreenshot) {
        if (isTakeScreenshot)
            ExtentReportListeners.test.get().pass(stepDescription, MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64Image(driver)).build());
        else ExtentReportListeners.test.get().pass(stepDescription);
    }

    private void reportStepFail(WebDriver driver, String stepDescription, boolean isTakeScreenshot) {
        if (isTakeScreenshot || driver != null)
            ExtentReportListeners.test.get().fail(stepDescription, MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64Image(driver)).build());
        else ExtentReportListeners.test.get().fail(stepDescription);
        ExtentReportListeners.summaryTest.get().fail(stepDescription);
    }

    private void reportStepSkip(WebDriver driver, String stepDescription, boolean isTakeScreenshot) {
        if (isTakeScreenshot)
            ExtentReportListeners.test.get().skip(stepDescription, MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64Image(driver)).build());
        else ExtentReportListeners.test.get().skip(stepDescription);
        ExtentReportListeners.summaryTest.get().skip(stepDescription);
    }

    private void reportStepWarning(WebDriver driver, String stepDescription, boolean isTakeScreenshot) {
        if (isTakeScreenshot || driver != null)
            ExtentReportListeners.test.get().warning(stepDescription, MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64Image(driver)).build());
        else ExtentReportListeners.test.get().warning(stepDescription);
        ExtentReportListeners.summaryTest.get().warning(stepDescription);
    }

    private void reportStepInfo(WebDriver driver, String stepDescription, boolean isTakeScreenshot) {
        if (isTakeScreenshot)
            ExtentReportListeners.test.get().info(stepDescription, MediaEntityBuilder.createScreenCaptureFromBase64String(getBase64Image(driver)).build());
        else ExtentReportListeners.test.get().info(stepDescription);
    }

    private void reportStepLog(LinkedHashMap<String, String> linkedHashMapTestParameters) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = (JSONObject) new JSONParser().parse(JSONObject.toJSONString(linkedHashMapTestParameters));
        } catch (ParseException e) {
        }
        ExtentReportListeners.test.get().log(Status.INFO, getCollapsibleMarkUp(String.valueOf(jsonObject)));
    }

    private String getCollapsibleMarkUp(String json) {
        counter++;
        return "<div class='container'><details><summary>Step Logs</summary><div class='json-tree' id='code-block-json-" + counter + "'></div><script>function jsonTreeCreate" + counter + "(){document.getElementById('code-block-json-" + counter + "').innerHTML = JSONTree.create(" + json + ");}jsonTreeCreate" + counter + "();</script></details></div>";
    }

    public synchronized String getBase64Image(WebDriver webDriver) {
        return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BASE64);
    }

    public void reportStepAndThrowException(Exception e, String reportStep, LogLevelEnum logLevelEnum) {
        ReportUtils.getInstance().reportStepWithoutScreenshot(reportStep, logLevelEnum);
        throw new RuntimeException(e);
    }
}