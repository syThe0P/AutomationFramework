package org.pom.factory.abstractfactory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.pom.enums.ConfigEnum;

import java.util.HashMap;

public class ChromeDriverManagerAbstract extends DriverManagerAbstract {

    @Override
    protected void startDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Optional: Enable headless mode from config
        boolean isHeadless = Boolean.parseBoolean(ConfigEnum.IS_HEADLESS.getValue());
        if (isHeadless) {
            options.addArguments("--headless=new");
        }

        // Basic arguments
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        // Optional: download directory if needed
        // HashMap<String, Object> prefs = new HashMap<>();
        // prefs.put("download.default_directory", "your/download/path");
        // options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
    }
}
