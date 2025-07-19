package org.pom.base;

import org.openqa.selenium.WebDriver;
import org.pom.enums.BrowserEnum;
import org.pom.enums.ConfigEnum;
import org.pom.factory.abstractfactory.DriverManagerAbstract;
import org.pom.factory.abstractfactory.DriverManagerFactoryAbstract;
import org.pom.utils.seleniumutils.WaitUtilities;
import org.testng.annotations.*;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Logger;

public class BaseTest {
    public static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static ClientApi api;
    private static Set<String> fetchedUrls = new HashSet<>();
    public static final String CONTEXT_NAME = "new_context";
    private static final ThreadLocal<Boolean> isFail = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private static final ThreadLocal<ArrayList<Throwable>> exceptions = ThreadLocal.withInitial(() -> new ArrayList<Throwable>());
    private static final ThreadLocal<Integer> count = ThreadLocal.withInitial(() -> 0);
    public static LinkedHashMap<String, String> linkedHashMapMasterTestData = new LinkedHashMap<>();

    private final ThreadLocal<DriverManagerAbstract> driverManager = new ThreadLocal<>();
    private final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    protected WebDriver getDriver() {
        return driver.get();
    }

    private void setDriver(WebDriver driver) {
        this.driver.set(driver);
    }

    private void setDriverManager(DriverManagerAbstract manager) {
        this.driverManager.set(manager);
    }

    private DriverManagerAbstract getDriverManager() {
        return this.driverManager.get();
    }

    protected synchronized void setIsFail(Boolean isFail) {
        BaseTest.isFail.set(isFail);
    }

    protected synchronized ArrayList<Throwable> getExceptions() {
        return exceptions.get();
    }

    protected synchronized void addException(Throwable throwable) {
        this.getExceptions().add(throwable);
    }

    protected synchronized Integer getCount() {
        return count.get();
    }

    protected synchronized void setCount(Integer count) {
        BaseTest.count.set(count);
    }

    @BeforeMethod(alwaysRun = true)
    public void startDriver() {
        logger.info("Starting driver on thread: " + Thread.currentThread().getName());
        setDriverManager(DriverManagerFactoryAbstract.getManager(BrowserEnum.valueOf(ConfigEnum.BROWSER.getValue())));
        setDriver(getDriverManager().getDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void quitDriver() {
        logger.info("Quitting driver on thread: " + Thread.currentThread().getName());
        if (getDriver() != null) {
            getDriver().quit();
        }
        driver.remove();
        driverManager.remove();
    }

    public static void setFetchedUrls(String fetchedUrl) {
            try {
                waitForSpideringToBeComplete(getClientApi().spider.scan(fetchedUrl, null, "True", null, "True"));
                getClientApi().core.accessUrl(fetchedUrl, "true");
                WaitUtilities.getInstance(new BaseTest().getDriver()).applyStaticWait(4);
                getClientApi().context.includeInContext(CONTEXT_NAME, fetchedUrl+".*");
                WaitUtilities.getInstance(new BaseTest().getDriver()).applyStaticWait(4);
                BaseTest.fetchedUrls.add(fetchedUrl);
            }
            catch (ClientApiException e) {
                logger.info("Exception in : " + e.getMessage());
            }
    }

    public static void waitForSpideringToBeComplete(ApiResponse resp) {
        String scanId = ((ApiResponseElement) resp).getValue();
        try {
            int progress = 0;
            while (progress < 100) {
                WaitUtilities.getInstance(new BaseTest().getDriver()).applyStaticWait(1);
                ApiResponse status = null;
                status = getClientApi().spider.status(scanId);

                progress = Integer.parseInt(((ApiResponseElement) status).getValue());
            }
        } catch (ClientApiException e) {
            logger.info("Exception occurred while waiting for spidering to be complete: " + e.getMessage());
        }
    }

    public static ClientApi getClientApi() {
        return api;
    }
}
