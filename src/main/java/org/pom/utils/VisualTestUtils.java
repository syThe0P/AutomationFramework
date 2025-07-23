package org.pom.utils;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.pom.enums.LogLevelEnum;
import org.pom.listeners.ExtentReportListeners;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

public class VisualTestUtils {
    private static final String BASELINE_DIR = "src/test/resources/baseline_screenshots/";
    private static final String DIFF_DIR = "test-output/visual-diffs/";

    public static boolean compareWithBaseline(WebDriver driver, String pageName) {
        try {
            File actual = new File(DIFF_DIR + pageName + "_actual.png");
            File diff = new File(DIFF_DIR + pageName + "_diff.png");

            // Ensure directories exist
            actual.getParentFile().mkdirs();
            diff.getParentFile().mkdirs();

            // Capture current screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), actual.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Use the actual screenshot filename for baseline image
            String actualFileName = actual.getName(); // e.g., LoginPage_actual.png
            String baseImageName = actualFileName.replace("_actual", ""); // e.g., LoginPage.png
            File baseline = new File(BASELINE_DIR + baseImageName);
            baseline.getParentFile().mkdirs();

            if (!baseline.exists()) {
                // First run: save current screenshot as baseline
                Files.copy(actual.toPath(), baseline.toPath(), StandardCopyOption.REPLACE_EXISTING);
                ReportUtils.getInstance().reportStep(driver, "Baseline screenshot created for <b>" + baseImageName + "</b>.", LogLevelEnum.INFO);
                return true;
            }

            BufferedImage expectedImg = ImageIO.read(baseline);
            BufferedImage actualImg = ImageIO.read(actual);
            ImageComparison comparison = new ImageComparison(expectedImg, actualImg);
            ImageComparisonResult result = comparison.compareImages();
            ImageComparisonState state = result.getImageComparisonState();

            // --- Begin: Added metrics calculation ---
            int width = Math.min(expectedImg.getWidth(), actualImg.getWidth());
            int height = Math.min(expectedImg.getHeight(), actualImg.getHeight());
            int diffPixels = 0;
            int minX = width, minY = height, maxX = -1, maxY = -1;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (expectedImg.getRGB(x, y) != actualImg.getRGB(x, y)) {
                        diffPixels++;
                        if (x < minX) minX = x;
                        if (y < minY) minY = y;
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;
                    }
                }
            }
            int totalPixels = width * height;
            double diffPercent = totalPixels > 0 ? (100.0 * diffPixels / totalPixels) : 0;
            String boundingBox = (diffPixels > 0) ? "(" + minX + "," + minY + ") to (" + maxX + "," + maxY + ")" : "N/A";
            String diffDetails = "<br><b>Diff Pixels:</b> " + diffPixels + " (" + String.format("%.2f", diffPercent) + "%)" +
                    "<br><b>Bounding Box:</b> " + boundingBox;
            // --- End: Added metrics calculation ---

            if (state != ImageComparisonState.MATCH) {
                BufferedImage diffImg = result.getResult();
                if (diffImg != null) {
                    ImageIO.write(diffImg, "png", diff);
                    String base64Diff = encodeImageToBase64(diff);
                    String base64Baseline = encodeImageToBase64(baseline);
                    String base64Actual = encodeImageToBase64(actual);

                    // Log the result state and new metrics
                    String diffMetrics = "<br><b>Visual Diff State:</b> " + state + diffDetails;
                    ReportUtils.getInstance().reportStep(driver, "Visual difference found for <b>" + baseImageName + "</b> (see images below)" + diffMetrics, LogLevelEnum.INFO);
                    if (base64Baseline != null) {
                        ExtentReportListeners.test.get().info("Baseline image:",
                                com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(base64Baseline).build());
                    }
                    if (base64Actual != null) {
                        ExtentReportListeners.test.get().info("Actual image:",
                                com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(base64Actual).build());
                    }
                    if (base64Diff != null) {
                        ExtentReportListeners.test.get().info("Diff image:",
                                com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(base64Diff).build());
                    }
                } else {
                    ReportUtils.getInstance().reportStep(driver, "Visual difference found for <b>" + baseImageName + "</b> but diff image could not be generated.", LogLevelEnum.INFO);
                }
                return false;
            } else {
                ReportUtils.getInstance().reportStep(driver, "No visual difference for <b>" + baseImageName + "</b>.", LogLevelEnum.PASS);
                return true;
            }
        } catch (Exception e) {
            ReportUtils.getInstance().reportStep(driver, "Visual comparison failed: " + e.getMessage(), LogLevelEnum.WARNING);
            return false;
        }
    }

    private static String encodeImageToBase64(File imageFile) {
        try {
            BufferedImage img = ImageIO.read(imageFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}