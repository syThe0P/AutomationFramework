package org.pom.utils.seleniumutils;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.pom.utils.OSInfoUtils;

public class KeyBoardActionUtils {

    protected WebDriver driver;

    public KeyBoardActionUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static KeyBoardActionUtils getInstance(WebDriver driver) {
        return new KeyBoardActionUtils(driver);
    }

    public static Keys getCommandKey() {
        if (OSInfoUtils.getInstance().getOSInfo().toLowerCase().contains("mac"))
            return Keys.COMMAND;
        else return Keys.CONTROL;
    }

    public boolean pressKey(WebElement element, Keys key, String alphabet) {
        WaitUtilities.getInstance(driver).waitForElementVisible(element);
        try {
            if (!alphabet.equals("")) {
                element.sendKeys(Keys.chord(key, alphabet));
                System.out.println("[INFO] ====> Pressed key combo: " + key + " + " + alphabet);
            } else {
                element.sendKeys(key);
                System.out.println("[INFO] ====> Pressed key: " + key);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] ====> Failed to press key: " + key + " due to " + e.getClass().getSimpleName());
            throw e;
        }
        return true;
    }

    public boolean pressKey(WebElement element, Keys key) {
        return pressKey(element, key, "");
    }

    public boolean pressKeyUsingActions(Keys key, String alphabet) {
        return pressKeyUsingActions(key, null, alphabet);
    }

    public boolean pressKeyUsingActions(String alphabet) {
        return pressKeyUsingActions(null, null, alphabet);
    }

    public boolean pressKeyUsingActions(Keys key) {
        return pressKeyUsingActions(key, null, "");
    }

    public boolean pressKeyUsingActions(Keys key, Keys key2) {
        return pressKeyUsingActions(key, key2, "");
    }

    public boolean pressKeyUsingActions(Keys key, Keys key2, String alphabet) {
        String keyDescription = "";
        Actions actions = new Actions(driver);
        try {
            if (key != null && key2 != null && !alphabet.isEmpty()) {
                keyDescription = key.name() + " + " + key2.name() + " + " + alphabet;
                actions.keyDown(key).keyDown(key2).sendKeys(Keys.chord(alphabet)).keyUp(key).keyUp(key2).build().perform();
            } else if (key != null && key2 != null) {
                keyDescription = key.name() + " + " + key2.name();
                actions.keyDown(key).keyDown(key2).keyUp(key).keyUp(key2).build().perform();
            } else if (key != null && !alphabet.isEmpty()) {
                keyDescription = key.name() + " + " + alphabet;
                actions.keyDown(key).sendKeys(Keys.chord(alphabet)).keyUp(key).build().perform();
            } else if (key != null) {
                keyDescription = key.name();
                actions.keyDown(key).keyUp(key).build().perform();
            } else {
                keyDescription = alphabet;
                actions.sendKeys(Keys.chord(alphabet)).build().perform();
            }
            System.out.println("[INFO] ====> Performed keyboard action: " + keyDescription);
        } catch (Exception e) {
            System.out.println("[ERROR] ====> Failed to press key: " + keyDescription + " due to " + e.getClass().getSimpleName());
            throw new ElementNotInteractableException(e.getMessage(), e.getCause());
        }
        return true;
    }

    public void clearSearchField(WebElement inputElement) {
        pressKey(inputElement, Keys.CONTROL, "A");
        pressKey(inputElement, Keys.BACK_SPACE);
        System.out.println("[INFO] ====> Cleared search field using CONTROL + A and BACK_SPACE");
    }

    public void clearSearchField() {
        pressKeyUsingActions(getCommandKey(), "A");
        pressKeyUsingActions(Keys.BACK_SPACE);
        System.out.println("[INFO] ====> Cleared search field using Actions and BACK_SPACE");
    }

    public void pressKeyboardKeyAndClick(WebElement element, Keys key) {
        new Actions(driver).keyDown(key).pause(500).click(element).build().perform();
        System.out.println("[INFO] ====> Pressed key: " + key + " and clicked element");
    }
}