package org.pom.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.WebDriver;
import org.pom.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.pom.utils.DateTimeUtils;

import java.io.File;

public class ExtentReportListeners implements ITestListener {

    private static ExtentReports extent;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    public static final ThreadLocal<ExtentTest> summaryTest = new ThreadLocal<>();
    private static String reportFilePath; // Store report path for reuse

    private void deleteOldReports() {
        File dir = new File("test-output");
        File[] files = dir.listFiles((d, name) -> name.startsWith("ExtentReport_") && name.endsWith(".html"));
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    @Override
    public void onStart(ITestContext context) {
        deleteOldReports();
        String timestamp = DateTimeUtils.getInstance().getCurrentDate("yyyy-MM-dd") + "_" + DateTimeUtils.getInstance().getCurrentTime();
        // Use relative path for portability
        reportFilePath = "test-output/ExtentReport_" + timestamp + ".html";
        File reportFile = new File(System.getProperty("user.dir"), reportFilePath);

        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
        spark.config().setReportName("Automation Test Results");
        spark.config().setDocumentTitle("Test Report");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Tester", "Pranav Kumar");
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();

        // Use the same reportFilePath from onStart
        File reportFile = new File(System.getProperty("user.dir"), reportFilePath);
        String absPath = reportFile.getAbsolutePath();
        String clickable = "file://" + absPath;

        // Verify if the report file exists
        if (reportFile.exists()) {
            System.out.println("\n===============================");
            System.out.println("🔗 Extent Report Link (Local): " + clickable);
            System.out.println("📍 Relative Path: ./" + reportFilePath);
            System.out.println("📂 Absolute Path: " + absPath);
            System.out.println("===============================\n");
        } else {
            System.out.println("\n===============================");
            System.out.println("⚠️ Report file not found at: " + absPath);
            System.out.println("===============================\n");
        }
    }

    public static void attachBase64Image(String base64, String title) {
        if (test.get() != null && base64 != null) {
            try {
                test.get().info(title, MediaEntityBuilder
                        .createScreenCaptureFromBase64String(base64).build());
            } catch (Exception e) {
                test.get().info(title + " (image attachment failed)");
            }
        }
    }

    public static void initReport(String fileName) {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("Test Results");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void createTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void removeTest() {
        test.remove();
    }

    public static void logStepWithScreenshot(String message, WebDriver driver) {
        ExtentTest extentTest = getTest();
        if (extentTest != null) {
            String base64 = ScreenshotUtils.captureBase64Screenshot(driver);
            extentTest.info(message, MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
        } else {
            System.out.println("⚠️ ExtentTest not initialized. Message: " + message);
        }
    }

    public static void logStepWithScreenshot(String message) {
        if (getTest() != null) {
            getTest().info(message);
        } else {
            System.out.println("⚠️ ExtentTest not initialized. Message: " + message);
        }
    }
}