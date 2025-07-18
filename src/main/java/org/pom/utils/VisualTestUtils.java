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
            File baseline = new File(BASELINE_DIR + pageName + ".png");
            File actual = new File(DIFF_DIR + pageName + "_actual.png");
            File diff = new File(DIFF_DIR + pageName + "_diff.png");

            // Ensure directories exist
            baseline.getParentFile().mkdirs();
            actual.getParentFile().mkdirs();
            diff.getParentFile().mkdirs();

            // Capture current screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), actual.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (!baseline.exists()) {
                // First run: save current screenshot as baseline
                Files.copy(actual.toPath(), baseline.toPath(), StandardCopyOption.REPLACE_EXISTING);
                ReportUtils.getInstance().reportStep(driver, "Baseline screenshot created for <b>" + pageName + "</b>.", LogLevelEnum.INFO);
                return true;
            }

            BufferedImage expectedImg = ImageIO.read(baseline);
            BufferedImage actualImg = ImageIO.read(actual);
            ImageComparison comparison = new ImageComparison(expectedImg, actualImg);
            ImageComparisonResult result = comparison.compareImages();
            ImageComparisonState state = result.getImageComparisonState();

            if (state != ImageComparisonState.MATCH) {
                BufferedImage diffImg = result.getResult();
                if (diffImg != null) {
                    ImageIO.write(diffImg, "png", diff);
                    String base64Diff = encodeImageToBase64(diff);

                    // Log the result state only (since other metrics are not exposed)
                    String diffMetrics = "<br><b>Visual Diff State:</b> " + state;

                    ReportUtils.getInstance().reportStep(driver, "Visual difference found for <b>" + pageName + "</b> (see diff image)" + diffMetrics, LogLevelEnum.INFO);
                    if (base64Diff != null) {
                        ExtentReportListeners.test.get().info("Diff image:",
                                com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(base64Diff).build());
                    }
                } else {
                    ReportUtils.getInstance().reportStep(driver, "Visual difference found for <b>" + pageName + "</b> but diff image could not be generated.", LogLevelEnum.INFO);
                }
                return false;
            } else {
                ReportUtils.getInstance().reportStep(driver, "No visual difference for <b>" + pageName + "</b>.", LogLevelEnum.PASS);
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
