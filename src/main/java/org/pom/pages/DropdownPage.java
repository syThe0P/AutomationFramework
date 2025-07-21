package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.base.BaseTest;
import org.pom.enums.LocatorEnum;
import org.pom.utils.ConfigLoader;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;
import org.pom.utils.seleniumutils.WaitUtilities;

public class DropdownPage extends BasePage {

    @FindBy(xpath = "//h3[contains(text(),'Dropdown List')]")
    private WebElement heading;

    public DropdownPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }

    public DropdownPage loadDropdownPage(){
        load(ConfigLoader.get("BASE_URL") +  "/dropdown");
        return this;
    }

    public boolean isHeaderVisible(){
        WaitUtilities.getInstance(driver).waitForElementVisible(heading);
        return ValidationCommonUtils.getInstance(driver).isDisplayed(heading, "Dropdown Page Header", "Dropdown Page");
    }

    public DropdownPage clickOnDropdown() {
        WebElement dropdown = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.ID.value(), "dropdown");
        PageCommonUtils.getInstance(driver).click(dropdown, "Dropdown", "Dropdown Page");
        return this;
    }

    public DropdownPage selectOptionFromDropdown(String option) {
        WebElement optionElement = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.XPATH.value(), "//option[contains(text(),'" + option + "')]");
        PageCommonUtils.getInstance(driver).click(optionElement, option, "Dropdown Page");
        return this;
    }
}
