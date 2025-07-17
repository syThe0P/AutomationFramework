package org.pom.utils.seleniumutils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;
import org.pom.enums.LogLevelEnum;
import org.pom.utils.ReportUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.function.Function;

public class XPathUtils {
    protected WebDriver driver;
    private static final String PARENT_AXIS = "parent::";
    private static final String ANCESTOR_AXIS = "ancestor::";
    private static final String XPATH_PREFIX = "//";

    public XPathUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static XPathUtils getInstance(WebDriver driver) {
        return new XPathUtils(driver);
    }

    public static <T> T findElementWithFallback(WebDriver driver, Object originalLocator,
                                                String[] alternativeXPaths, Function<WebElement, T> action) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String pageClassMethodName = stackTrace[2].getMethodName();
        String testMethodName = stackTrace.length > 3 ? stackTrace[3].getMethodName() : "unknownTestMethod";
        String testClassName = stackTrace.length > 3 ? stackTrace[3].getClassName() : "unknownTestClass";
        String moduleName = extractModuleName(testClassName);

        // Extract original XPath from input
        String originalXPath = extractOriginalXPath(originalLocator);

        // Try all available XPath strategies in sequence
        try {
            // 1. Try cached XPath if available
            WebElement element = tryWithCachedXPath(driver, moduleName, testMethodName, pageClassMethodName);
            if (element != null) {
                return action.apply(element);
            }

            // 2. Try original XPath directly
            element = tryWithOriginalXPath(driver, originalXPath, moduleName, testMethodName, pageClassMethodName);
            if (element != null) {
                return action.apply(element);
            }

            // 3. Try provided alternative XPaths if any
            element = tryWithAlternativeXPaths(driver, alternativeXPaths, moduleName, testMethodName, pageClassMethodName);
            if (element != null) {
                return action.apply(element);
            }

            // 4. Try dynamic tag fallback strategy
            element = tryWithDynamicTagFallback(driver, originalXPath, moduleName, testMethodName, pageClassMethodName);
            if (element != null) {
                return action.apply(element);
            }

            // 5. Try ancestor fallback strategy if applicable
            element = tryWithAncestorFallback(driver, originalXPath, moduleName, testMethodName, pageClassMethodName);
            if (element != null) {
                return action.apply(element);
            }
        } catch (Exception e) {
            // Log exception if needed
            ReportUtils.getInstance().reportStep(driver, "Exception during element search: " + e.getMessage(), LogLevelEnum.FAIL);
        }

        // Final failure if no match found
        throw new NoSuchElementException("Element not found with any provided or fallback XPaths");
    }

    private static String extractOriginalXPath(Object originalLocator) {
        if (originalLocator instanceof String string) {
            return string;
        } else if (originalLocator instanceof WebElement webElement) {
            String xpath = extractXPathFromWebElement(webElement);
            if (xpath == null) {
                throw new IllegalArgumentException("Unable to extract or convert locator to XPath from provided WebElement");
            }
            return xpath;
        } else {
            throw new IllegalArgumentException("originalLocator must be either a String or WebElement");
        }
    }

    private static WebElement tryWithCachedXPath(WebDriver driver, String moduleName, String testMethodName, String pageClassMethodName) {
        String cachedXPath = XPathCache.getOptimizedXPath(moduleName, testMethodName, pageClassMethodName);
        if (cachedXPath != null) {
            try {
                WebElement element = driver.findElement(By.xpath(cachedXPath));
                ReportUtils.getInstance().reportStep(driver, "Using cached XPath: " + cachedXPath, LogLevelEnum.INFO);
                return element;
            } catch (NoSuchElementException e) {
                ReportUtils.getInstance().reportStep(driver, "Cached XPath failed, removing from cache: " + cachedXPath, LogLevelEnum.INFO);
                XPathCache.storeOptimizedXPath(moduleName, testMethodName, pageClassMethodName, null);
            }
        }
        return null;
    }

    private static WebElement tryWithOriginalXPath(WebDriver driver, String originalXPath, String moduleName, String testMethodName, String pageClassMethodName) {
        try {
            WebElement element = driver.findElement(By.xpath(originalXPath));
            ReportUtils.getInstance().reportStep(driver, "Using original XPath: " + originalXPath, LogLevelEnum.INFO);
            XPathCache.storeOptimizedXPath(moduleName, testMethodName, pageClassMethodName, originalXPath);
            return element;
        } catch (NoSuchElementException e) {
            ReportUtils.getInstance().reportStep(driver, "Original XPath not found: " + originalXPath, LogLevelEnum.INFO);
            return null;
        }
    }

    private static WebElement tryWithAlternativeXPaths(WebDriver driver, String[] alternativeXPaths, String moduleName, String testMethodName, String pageClassMethodName) {
        if (alternativeXPaths == null || alternativeXPaths.length == 0) {
            return null;
        }

        for (String alternativeXPath : alternativeXPaths) {
            try {
                WebElement element = driver.findElement(By.xpath(alternativeXPath));
                ReportUtils.getInstance().reportStep(driver, "Using alternative XPath: " + alternativeXPath, LogLevelEnum.INFO);
                XPathCache.storeOptimizedXPath(moduleName, testMethodName, pageClassMethodName, alternativeXPath);
                return element;
            } catch (NoSuchElementException ignored) {
                // Continue to next alternative
            }
        }
        return null;
    }

    private static WebElement tryWithDynamicTagFallback(WebDriver driver, String originalXPath, String moduleName, String testMethodName, String pageClassMethodName) {
        String originalTag = extractTagName(originalXPath);
        String[] alternativeTagNames = {"span", "div", "h2", "h1", "a", "button", "ul", "p"};

        for (String tag : alternativeTagNames) {
            if (tag.equalsIgnoreCase(originalTag)) {
                continue;
            }

            String tagSwitchedXPath = originalXPath.replaceFirst(XPATH_PREFIX + originalTag, XPATH_PREFIX + tag);
            try {
                WebElement element = driver.findElement(By.xpath(tagSwitchedXPath));
                ReportUtils.getInstance().reportStep(driver, "Trying tag switched XPath (fallback): " + tagSwitchedXPath, LogLevelEnum.INFO);
                XPathCache.storeOptimizedXPath(moduleName, testMethodName, pageClassMethodName, tagSwitchedXPath);
                return element;
            } catch (NoSuchElementException e) {
                ReportUtils.getInstance().reportStep(driver, "Tag switched XPath failed: " + tagSwitchedXPath, LogLevelEnum.INFO);
            }
        }
        return null;
    }

    private static WebElement tryWithAncestorFallback(WebDriver driver, String originalXPath, String moduleName, String testMethodName, String pageClassMethodName) {
        boolean hasParent = originalXPath.contains(PARENT_AXIS);
        if (!hasParent) {
            return null;
        }

        // Try with original tag but ancestor instead of parent
        String ancestorReplacedXPath = originalXPath.replace(PARENT_AXIS, ANCESTOR_AXIS);
        WebElement element = tryXPathWithReporting(driver, ancestorReplacedXPath, "Trying original tag with ancestor:: XPath", moduleName, testMethodName, pageClassMethodName);
        if (element != null) {
            return element;
        }

        // Try with different tags and ancestor
        return tryWithDynamicTagAndAncestor(driver, originalXPath, moduleName, testMethodName, pageClassMethodName);
    }

    private static WebElement tryWithDynamicTagAndAncestor(WebDriver driver, String originalXPath, String moduleName, String testMethodName, String pageClassMethodName) {
        String originalTag = extractTagName(originalXPath);
        String[] alternativeTagNames = {"span", "div", "h2", "h1", "a", "button", "ul", "p"};

        for (String tag : alternativeTagNames) {
            if (tag.equalsIgnoreCase(originalTag)) {
                continue;
            }

            String tagSwitchedXPath = originalXPath.replaceFirst(XPATH_PREFIX + originalTag, XPATH_PREFIX + tag);
            String tagAncestorXPath = tagSwitchedXPath.replace(PARENT_AXIS, ANCESTOR_AXIS);

            WebElement element = tryXPathWithReporting(driver, tagAncestorXPath, "Trying tag switched XPath with ancestor:: fallback", moduleName, testMethodName, pageClassMethodName);
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    private static WebElement tryXPathWithReporting(WebDriver driver, String xpath, String message, String moduleName, String testMethodName, String pageClassMethodName) {
        try {
            WebElement element = driver.findElement(By.xpath(xpath));
            ReportUtils.getInstance().reportStep(driver, message + ": " + xpath, LogLevelEnum.INFO);
            XPathCache.storeOptimizedXPath(moduleName, testMethodName, pageClassMethodName, xpath);
            return element;
        } catch (NoSuchElementException e) {
            ReportUtils.getInstance().reportStep(driver, message + " failed: " + xpath, LogLevelEnum.INFO);
            return null;
        }
    }

    private static String extractModuleName(String className) {
        int lastDot = className.lastIndexOf('.');
        if (lastDot == -1) return "default";
        String[] packageParts = className.substring(0, lastDot).split("\\.");
        return packageParts.length == 0 ? "default" : packageParts[packageParts.length - 1];
    }

    private static String extractTagName(String xpath) {
        return xpath.replaceAll("^" + XPATH_PREFIX + "(\\w+).*", "$1");
    }

    private static String extractXPathFromWebElement(WebElement element) {
        try {
            if (Proxy.isProxyClass(element.getClass())) {
                InvocationHandler handler = Proxy.getInvocationHandler(element);
                if (handler instanceof LocatingElementHandler) {
                    Field locatorField = LocatingElementHandler.class.getDeclaredField("locator");
                    // Comment explaining why accessibility modification is necessary
                    // Cannot access Selenium's private fields without this
                    locatorField.setAccessible(true);
                    Object locator = locatorField.get(handler);

                    Field byField = locator.getClass().getDeclaredField("by");
                    // Cannot access Selenium's private fields without this
                    byField.setAccessible(true);
                    Object by = byField.get(locator);

                    if (by instanceof By.ByXPath) {
                        return by.toString().replace("By.xpath: ", "");
                    } else if (by instanceof By.ById) {
                        return XPATH_PREFIX + "*[@id='" + by.toString().replace("By.id: ", "") + "']";
                    } else if (by instanceof By.ByCssSelector) {
                        return convertCssToXPath(by.toString().replace("By.cssSelector: ", ""));
                    } else if (by instanceof By.ByClassName) {
                        return XPATH_PREFIX + "*[contains(@class, '" + by.toString().replace("By.className: ", "") + "')]";
                    }
                }
            }
        } catch (Exception ignored) {
            // Reflection exceptions are expected when analyzing Selenium WebElement proxies
            // The method will return null, and callers will handle this by using alternative strategies
        }
        return null;
    }

    private static String convertCssToXPath(String css) {
        String[] parts = css.trim().split("\\s+");
        StringBuilder xpath = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.startsWith("#")) {
                xpath.append(XPATH_PREFIX).append("*[@id='").append(part.substring(1)).append("']");
            } else if (part.startsWith(".")) {
                xpath.append(XPATH_PREFIX).append("*[contains(@class, '").append(part.substring(1)).append("')]");
            } else {
                xpath.append(XPATH_PREFIX).append(part);
            }
            if (i < parts.length - 1) xpath.append("/");
        }
        return xpath.toString();
    }
}