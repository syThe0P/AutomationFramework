package org.pom.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.pom.utils.DateTimeUtils;


import java.io.File;

public class ExtentReportListeners implements ITestListener {

    private static ExtentReports extent;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    public static final ThreadLocal<ExtentTest> summaryTest = new ThreadLocal<>();
    private static String reportFilePath;

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
        reportFilePath = "test-output/ExtentReport_" + timestamp + ".html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
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
        String absPath = new java.io.File(reportFilePath).getAbsolutePath();
        String clickable = "file://" + absPath;
        System.out.println("\nReport link: " + clickable + "\n");
    }

    public static void attachBase64Image(String base64, String title) {
        if (test.get() != null && base64 != null) {
            try {
                test.get().info(title, com.aventstack.extentreports.MediaEntityBuilder
                        .createScreenCaptureFromBase64String(base64).build());
            } catch (Exception e) {
                test.get().info(title + " (image attachment failed)");
            }
        }
    }

}
