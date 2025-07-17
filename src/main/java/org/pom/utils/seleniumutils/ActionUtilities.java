package org.pom.utils.seleniumutils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ActionUtilities {

    String rightClick = "rightClick";
    String boldSuccessfullyText = " successfully.";
    String unableToPerformText = "Unable to perform ";
    String invalidActionNameText = "Invalid action name: ";

    Actions actions;
    protected WebDriver driver;

    public ActionUtilities(WebDriver driver) {
        this.driver = driver;
        actions = new Actions(driver);
    }

    public static ActionUtilities getInstance(WebDriver driver) {
        return new ActionUtilities(driver);
    }

    // *********** Action Events ******************

    public void moveToElement(WebElement element, String elementName) {
        movementActions("moveToElement", element, null, elementName, null);
    }

    public void dragAndDropElement(WebElement fromElement, WebElement toElement, String fromElementName, String toElementName) {
        movementActions("dragAndDropElement", fromElement, toElement, fromElementName, toElementName);
    }

    public void dragAndDropBy(WebElement element, String elementName, int xOffset, int yOffset) {
        movementCoordinateActions("dragAndDropBy", element, elementName, xOffset, yOffset);
    }

    public void doubleClick(WebElement element, String clickedElementName, String clickedFrom) {
        clickActions("doubleClick", element, clickedElementName, clickedFrom);
    }

    public void rightClick(WebElement element, String clickedElementName, String clickedFrom) {
        clickActions(rightClick, element, clickedElementName, clickedFrom);
    }

    public void rightClick(String pageName) {
        clickActions(rightClick, null, null, pageName);
    }

    private void clickActions(String actionName, WebElement element, String clickedElementName, String clickedFrom) {
        if (element != null) WaitUtilities.getInstance(driver).waitForElementVisible(element, clickedElementName);
        try {
            switch (actionName) {
                case "doubleClick" -> {
                    actions.doubleClick(element).build().perform();
                    System.out.println("✅ Double clicked on '" + clickedElementName + "' from '" + clickedFrom + "'.");
                }
                case "rightClick" -> {
                    if (element == null) {
                        actions.contextClick().build().perform();
                        System.out.println("✅ Right clicked on page: '" + clickedFrom + "'.");
                    } else {
                        actions.contextClick(element).build().perform();
                        System.out.println("✅ Right clicked on '" + clickedElementName + "' from '" + clickedFrom + "'.");
                    }
                }
                default -> System.out.println("❌ " + invalidActionNameText + actionName);
            }
        } catch (Exception e) {
            System.out.println("❌ " + unableToPerformText + actionName + " from " + clickedFrom + " due to: " + e.getClass().getSimpleName());
            throw e;
        }
    }

    private void movementActions(String actionName, WebElement fromElement, WebElement toElement, String fromElementName, String toElementName) {
        if (fromElement != null) WaitUtilities.getInstance(driver).waitForElementVisible(fromElement, fromElementName);
        try {
            switch (actionName) {
                case "moveToElement" -> {
                    actions.moveToElement(fromElement).build().perform();
                    System.out.println("✅ Moved to element '" + fromElementName + "'.");
                }
                case "dragAndDropElement" -> {
                    actions.dragAndDrop(fromElement, toElement).build().perform();
                    System.out.println("✅ Dragged element '" + fromElementName + "' and dropped to '" + toElementName + "'.");
                }
                default -> System.out.println("❌ " + invalidActionNameText + actionName);
            }
        } catch (Exception e) {
            System.out.println("❌ " + unableToPerformText + actionName + " due to: " + e.getClass().getSimpleName());
            throw e;
        }
    }

    private void movementCoordinateActions(String actionName, WebElement fromElement, String elementName, int xOffset, int yOffset) {
        if (fromElement != null) WaitUtilities.getInstance(driver).waitForElementVisible(fromElement, xOffset);
        try {
            switch (actionName) {
                case "dragAndDropBy" -> {
                    actions.dragAndDropBy(fromElement, xOffset, yOffset).build().perform();
                    System.out.println("✅ Dragged element '" + elementName + "' to offset (" + xOffset + ", " + yOffset + ").");
                }
                case "click" -> {
                    actions.moveToElement(fromElement, xOffset, yOffset).click().perform();
                    System.out.println("✅ Clicked element '" + elementName + "' at offset (" + xOffset + ", " + yOffset + ").");
                }
                default -> System.out.println("❌ " + invalidActionNameText + actionName);
            }
        } catch (Exception e) {
            System.out.println("❌ " + unableToPerformText + actionName + " due to: " + e.getClass().getSimpleName());
            throw e;
        }
    }

    public void click(WebElement element, String elementName, int xOffset, int yOffset) {
        movementCoordinateActions("click", element, elementName, xOffset, yOffset);
    }

    public void sendKeysToFocusedElement(String text) {
        try {
            new Actions(driver).sendKeys(text).perform();
            System.out.println("✅ Sent keys to focused element: '" + text + "'");
        } catch (Exception e) {
            System.out.println("❌ Unable to send keys due to: " + e.getClass().getSimpleName());
            throw e;
        }
    }
}
