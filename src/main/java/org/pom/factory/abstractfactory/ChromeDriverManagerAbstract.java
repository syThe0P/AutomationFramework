package org.pom.factory.abstractfactory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.pom.enums.ConfigEnum;

import java.util.HashMap;

public class ChromeDriverManagerAbstract extends DriverManagerAbstract {

    @Override
    protected void startDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // ✅ Headless check from config
        boolean isHeadless = Boolean.parseBoolean(ConfigEnum.IS_HEADLESS.getValue());
        if (isHeadless) {
            options.addArguments("--headless=new"); // modern headless
        }

        // ✅ Recommended arguments for Jenkins/macOS
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-debugging-port=0");
        options.addArguments("--disable-extensions");
        options.addArguments("--window-size=1920,1080"); // helpful for visual testing

        // ✅ Optional: prevent Dock icons (macOS)
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        // ✅ Optional: Set download dir (only if needed)
        // HashMap<String, Object> prefs = new HashMap<>();
        // prefs.put("download.default_directory", "/tmp/downloads");
        // options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
    }

}
