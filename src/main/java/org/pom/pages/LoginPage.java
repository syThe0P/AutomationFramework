package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.browsingcontext.Locator;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.base.BaseTest;
import org.pom.enums.LocatorEnum;
import org.pom.utils.ConfigLoader;
import org.pom.utils.VisualTestUtils;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;
import org.pom.utils.seleniumutils.WaitUtilities;
import org.pom.utils.seleniumutils.XPathUtils;

public class LoginPage extends BasePage {

    @FindBy(xpath = "//h2[contains(text(),'Login Page')]")
    private WebElement heading;

    @FindBy(xpath = "//p[@type='submit']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }

    public LoginPage loadLoginPage(){
        load(ConfigLoader.get("BASE_URL") + "/login");
        VisualTestUtils.compareWithBaseline(driver, "LoginPage");
        return this;
    }

    public boolean isHeaderVisible(){
        WaitUtilities.getInstance(driver).waitForElementVisible(heading);
        return ValidationCommonUtils.getInstance(driver).isDisplayed(heading, "Login Page Header", "Login Page");
    }

    public LoginPage enterTextInInputBox(String inputBoxId, String text) {
        WebElement inputBox = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.ID.value(), inputBoxId);
        PageCommonUtils.getInstance(driver).clearAndSendKeys(inputBox, "Input box with ID: " + inputBoxId, text);
        return this;
    }

    public LoginPage clickLoginButton() {
        WebElement clickLoginButton = XPathUtils.findElementWithFallback(driver, loginButton, null, element -> element);
        PageCommonUtils.getInstance(driver).click(clickLoginButton, "Login button", "Login Page");
        return this;
    }

    public boolean isErrorMessageDisplayed(String errorMessage) {
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//div[@id='flash' and contains(.,'" + errorMessage + "')]",LocatorEnum.XPATH.value(), "Error message: " + errorMessage, "Login Page");
    }

    public boolean verifyLoginSuccessful(){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//div[@id='flash success' and contains(.,'You logged into a secure area!')]",LocatorEnum.XPATH.value(), "You logged into a secure area!", "Login Page");
    }
}
