package org.pom.factory.abstractfactory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.pom.enums.ConfigEnum;

public class FirefoxDriverManagerAbstract extends DriverManagerAbstract {

    @Override
    protected void startDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        // Enable headless mode from config
        boolean isHeadless = Boolean.parseBoolean(ConfigEnum.IS_HEADLESS.getValue());
        if (isHeadless) {
            options.addArguments("-headless");
        }

        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        driver = new FirefoxDriver(options);

        if (isHeadless) {
            driver.manage().window().setSize(new Dimension(1600, 900));
        } else {
            driver.manage().window().maximize();
        }
    }
}
