package org.pom.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.openqa.selenium.WebDriver;
import org.pom.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.pom.utils.DateTimeUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Timer;
import java.util.TimerTask;

public class ExtentReportListeners implements ITestListener {

    private static ExtentReports extent;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    public static final ThreadLocal<ExtentTest> summaryTest = new ThreadLocal<>();
    private static String reportFilePath; // Store report path for reuse
    private static final String LOCAL_SERVER_PORT = "8000"; // Default port for local server
    private static final String JENKINS_URL = System.getenv("JENKINS_URL"); // Jenkins environment variable
    private static HttpServer server;
    private static final int PORT = 8000;
    private static final int AUTO_SHUTDOWN_MS = 20_000; // 20 seconds
    private static final String BASE_DIR = "test-output";

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
        File reportDir = new File("test-output");
        if (!reportDir.exists()) {
            reportDir.mkdirs(); // Create test-output directory if it doesn't exist
        }
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

    public static void startServer() throws IOException {
        if (server != null) return; // Prevent duplicate servers

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new FileHandler(BASE_DIR));
        server.setExecutor(null); // default executor
        server.start();

        System.out.println("✅ Report server started at: http://127.0.0.1:" + PORT + "/");

        // Auto-shutdown logic
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopServer();
            }
        }, AUTO_SHUTDOWN_MS);
    }

    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println("🛑 Report server stopped after 20 seconds.");
        }
    }

    static class FileHandler implements HttpHandler {
        private final String baseDir;

        public FileHandler(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            File file = new File(baseDir, path.equals("/") ? "ExtentReport_latest.html" : path);

            if (!file.exists()) {
                String response = "404 (Not Found)\n";
                exchange.sendResponseHeaders(404, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
                return;
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush(); // your existing report flush logic

        File reportFile = new File(System.getProperty("user.dir"), reportFilePath);
        String reportFileName = reportFile.getName();

        // Start server only in local (non-Jenkins)
        if (JENKINS_URL == null || JENKINS_URL.isEmpty()) {
            try {
                startServer();
            } catch (IOException e) {
                System.out.println("⚠️ Could not start local HTTP server: " + e.getMessage());
            }
        }

        if (reportFile.exists()) {
            System.out.println("\n===============================");
            String reportUrl = generateReportUrl(reportFileName);
            System.out.println("🔗 Extent Report Link: " + reportUrl);
            System.out.println("📍 Relative Path: ./" + reportFilePath);
            System.out.println("📂 Absolute Path: " + reportFile.getAbsolutePath());
            System.out.println("===============================\n");
        } else {
            System.out.println("\n===============================");
            System.out.println("⚠️ Report file not found at: " + reportFile.getAbsolutePath());
            System.out.println("===============================\n");
        }
    }

    private String generateReportUrl(String reportFileName) {
        if (JENKINS_URL != null && !JENKINS_URL.isEmpty()) {
            String jobName = System.getenv("JOB_NAME") != null ? System.getenv("JOB_NAME") : "your-job-name";
            String buildNumber = System.getenv("BUILD_NUMBER") != null ? System.getenv("BUILD_NUMBER") : "latest";
            return JENKINS_URL + "/job/" + jobName + "/" + buildNumber + "/htmlreports/Automation_Test_Report/" + reportFileName;
        } else {
            return "http://127.0.0.1:" + PORT + "/" + reportFileName;
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
//package org.pom.listeners;
//
//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.MediaEntityBuilder;
//import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import com.aventstack.extentreports.reporter.configuration.Theme;
//import org.openqa.selenium.WebDriver;
//import org.pom.utils.ScreenshotUtils;
//import org.testng.ITestContext;
//import org.testng.ITestListener;
//import org.testng.ITestResult;
//import org.pom.utils.DateTimeUtils;
//
//import java.io.File;
//
//public class ExtentReportListeners implements ITestListener {
//
//    private static ExtentReports extent;
//    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
//    public static final ThreadLocal<ExtentTest> summaryTest = new ThreadLocal<>();
//    private static String reportFilePath; // Store report path for reuse
//
//    private void deleteOldReports() {
//        File dir = new File("test-output");
//        File[] files = dir.listFiles((d, name) -> name.startsWith("ExtentReport_") && name.endsWith(".html"));
//        if (files != null) {
//            for (File f : files) {
//                f.delete();
//            }
//        }
//    }
//
//    @Override
//    public void onStart(ITestContext context) {
//        deleteOldReports();
//        String timestamp = DateTimeUtils.getInstance().getCurrentDate("yyyy-MM-dd") + "_" + DateTimeUtils.getInstance().getCurrentTime();
//        // Use relative path for portability
//        reportFilePath = "test-output/ExtentReport_" + timestamp + ".html";
//        File reportFile = new File(System.getProperty("user.dir"), reportFilePath);
//
//        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
//        spark.config().setReportName("Automation Test Results");
//        spark.config().setDocumentTitle("Test Report");
//
//        extent = new ExtentReports();
//        extent.attachReporter(spark);
//        extent.setSystemInfo("Tester", "Pranav Kumar");
//    }
//
//    @Override
//    public void onTestStart(ITestResult result) {
//        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
//        test.set(extentTest);
//    }
//
//    @Override
//    public void onTestSuccess(ITestResult result) {
//        test.get().pass("Test passed");
//    }
//
//    @Override
//    public void onTestFailure(ITestResult result) {
//        test.get().fail(result.getThrowable());
//    }
//
//    @Override
//    public void onTestSkipped(ITestResult result) {
//        test.get().skip(result.getThrowable());
//    }
//
//    @Override
//    public void onFinish(ITestContext context) {
//        extent.flush();
//
//        // Use the same reportFilePath from onStart
//        File reportFile = new File(System.getProperty("user.dir"), reportFilePath);
//        String absPath = reportFile.getAbsolutePath();
//        String clickable = "file://" + absPath;
//
//        // Verify if the report file exists
//        if (reportFile.exists()) {
//            System.out.println("\n===============================");
//            System.out.println("🔗 Extent Report Link (Local): " + clickable);
//            System.out.println("📍 Relative Path: ./" + reportFilePath);
//            System.out.println("📂 Absolute Path: " + absPath);
//            System.out.println("===============================\n");
//        } else {
//            System.out.println("\n===============================");
//            System.out.println("⚠️ Report file not found at: " + absPath);
//            System.out.println("===============================\n");
//        }
//    }
//
//    public static void attachBase64Image(String base64, String title) {
//        if (test.get() != null && base64 != null) {
//            try {
//                test.get().info(title, MediaEntityBuilder
//                        .createScreenCaptureFromBase64String(base64).build());
//            } catch (Exception e) {
//                test.get().info(title + " (image attachment failed)");
//            }
//        }
//    }
//
//    public static void initReport(String fileName) {
//        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);
//        sparkReporter.config().setTheme(Theme.STANDARD);
//        sparkReporter.config().setDocumentTitle("Automation Report");
//        sparkReporter.config().setReportName("Test Results");
//
//        extent = new ExtentReports();
//        extent.attachReporter(sparkReporter);
//    }
//
//    public static void flushReport() {
//        if (extent != null) {
//            extent.flush();
//        }
//    }
//
//    public static void createTest(String testName) {
//        ExtentTest extentTest = extent.createTest(testName);
//        test.set(extentTest);
//    }
//
//    public static ExtentTest getTest() {
//        return test.get();
//    }
//
//    public static void removeTest() {
//        test.remove();
//    }
//
//    public static void logStepWithScreenshot(String message, WebDriver driver) {
//        ExtentTest extentTest = getTest();
//        if (extentTest != null) {
//            String base64 = ScreenshotUtils.captureBase64Screenshot(driver);
//            extentTest.info(message, MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
//        } else {
//            System.out.println("⚠️ ExtentTest not initialized. Message: " + message);
//        }
//    }
//
//    public static void logStepWithScreenshot(String message) {
//        if (getTest() != null) {
//            getTest().info(message);
//        } else {
//            System.out.println("⚠️ ExtentTest not initialized. Message: " + message);
//        }
//    }
//}