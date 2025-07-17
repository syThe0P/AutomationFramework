package org.pom.utils.seleniumutils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.File;

public class FileUploadUtils {

    Actions actions;

    protected WebDriver driver;

    public FileUploadUtils(WebDriver driver) {
        this.driver = driver;
        actions = new Actions(driver);
    }

    public static FileUploadUtils getInstance(WebDriver driver) {
        return new FileUploadUtils(driver);
    }

    public synchronized void uploadFile(WebElement element, String fileName) {
        try {
            // You can keep files inside: src/test/resources/uploadFiles/
            String basePath = "src/test/resources/uploadFiles/";
            String absolutePath = new File(basePath + fileName).getAbsolutePath();

            element.sendKeys(absolutePath);

            System.out.println("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            System.out.println("❌ Failed to upload file: " + fileName + " - " + e.getMessage());
            throw new RuntimeException("File upload failed", e);
        }
    }
}