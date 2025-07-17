package org.pom.utils.seleniumutils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DropDownUtils {

    protected WebDriver driver;

    public DropDownUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static DropDownUtils getInstance(WebDriver driver) {
        return new DropDownUtils(driver);
    }

    // ***************************** DropDownUtils ************************//

    private boolean selectDropDownOption(WebElement element, String selectedElementName, String selectedFrom, String selectBy, String value) {
        WaitUtilities.getInstance(driver).waitForElementClickable(element, selectedElementName);
        try {
            Select select = new Select(element);
            switch (selectBy.toLowerCase()) {
                case "selectbyvisibletext" -> {
                    select.selectByVisibleText(value);
                    System.out.println("✅ Selected '" + value + "' by visible text in '" + selectedElementName + "' from '" + selectedFrom + "'.");
                }
                case "selectbyvalue" -> {
                    select.selectByValue(value);
                    System.out.println("✅ Selected value '" + value + "' in '" + selectedElementName + "' from '" + selectedFrom + "'.");
                }
                case "selectbyindex" -> {
                    int index = Integer.parseInt(value);
                    select.selectByIndex(index);
                    System.out.println("✅ Selected index " + index + " in '" + selectedElementName + "' from '" + selectedFrom + "'.");
                }
                default -> {
                    System.out.println("❌ Invalid 'selectBy' type: " + selectBy + " provided for dropdown.");
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Unable to perform dropdown selection using '" + selectBy + "' on '" + selectedElementName + "' from '" + selectedFrom + "' due to: " + e.getClass().getSimpleName());
            throw e;
        }
        return true;
    }

    public boolean selectDropDownByVisibleText(WebElement element, String visibleText, String selectedElementName, String selectedFrom) {
        return selectDropDownOption(element, selectedElementName, selectedFrom, "selectByVisibleText", visibleText);
    }

    public boolean selectDropDownByIndex(WebElement element, String index, String selectedElementName, String selectedFrom) {
        return selectDropDownOption(element, selectedElementName, selectedFrom, "selectByIndex", index);
    }

    public boolean selectDropDownByValue(WebElement element, String value, String selectedElementName, String selectedFrom) {
        return selectDropDownOption(element, selectedElementName, selectedFrom, "selectByValue", value);
    }

    public int getCountOfDropdownOptions(WebElement element, String selectedElementName, String selectedFrom) {
        Select select = new Select(element);
        int count = select.getOptions().size();
        System.out.println("ℹ️ Number of options in '" + selectedElementName + "' from '" + selectedFrom + "': " + count);
        return count;
    }

    public String getSelectedOptionText(WebElement element, String selectedFrom) {
        Select select = new Select(element);
        String option = select.getFirstSelectedOption().getText();
        System.out.println("ℹ️ Selected option from '" + selectedFrom + "' is: " + option);
        return option;
    }
}
