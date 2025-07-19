package org.pom.listeners;


import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.WebDriver;
import org.pom.utils.ScreenshotUtils;
import org.testng.*;

public class TestListeners implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportListeners.initReport("test-output/ExtentReport_" + System.currentTimeMillis() + ".html");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportListeners.flushReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportListeners.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportListeners.getTest().pass("Test passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportListeners.getTest().fail(result.getThrowable());

        WebDriver driver = (WebDriver) result.getTestContext().getAttribute("driver");
        if (driver != null) {
            String base64Screenshot = ScreenshotUtils.captureBase64Screenshot(driver);
            ExtentReportListeners.getTest().addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportListeners.getTest().skip("Test skipped");
    }

    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onTestFailedWithTimeout(ITestResult result) {}
}
