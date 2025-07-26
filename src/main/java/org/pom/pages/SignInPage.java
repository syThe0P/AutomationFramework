package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.enums.LocatorEnum;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;
import org.pom.utils.seleniumutils.WaitUtilities;

public class SignInPage extends BasePage {

    @FindBy(xpath = "//div/form/div[contains(@class,'flex justify-center')]/*")
    private WebElement headingImage;

    @FindBy(id = "login-btn")
    private WebElement loginInButton;

    public SignInPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public SignInPage loadSignInPage() {
        load("https://testathon.live/signin"); // Replace with actual sign-in URL
        return this;
    }

    public boolean isHeaderImageVisible(){
        return ValidationCommonUtils.getInstance(driver).isDisplayed(headingImage, "Login Page Header", "Login Page");
    }

    public SignInPage enterTextInUserNameOrPasswordInputBox(String placeholderName, String inputText) {
        WebElement inputBox = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.XPATH.value(), "//div[contains(text(),'"+placeholderName+"')]/parent::div[contains(@class,'css-1hwfws3')]");
        PageCommonUtils.getInstance(driver).clearAndSendKeys(inputBox, "Input box : " + placeholderName, inputText);
        return this;
    }

    public DemoPage clickOnLogInButton() {
        PageCommonUtils.getInstance(driver).click(loginInButton, "Login In button", "Sign In Page");
        return new DemoPage(driver);
    }

    //errorMessage can be "Invalid Username" or "Invalid Password"
    public boolean isErrorMessageDisplayed(String errorMessage) {
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//h3[contains(text(),'" + errorMessage + "')]", LocatorEnum.XPATH.value(), "Error message: " + errorMessage, "Sign In Page");
    }

    public SignInPage clickOnUserNameOrPasswordDropdown(String inputBoxId, String placeholderName) {
        WebElement dropdown = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.XPATH.value(), "//div[@id='" + inputBoxId + "']//div[contains(text(),'" + placeholderName + "')]");
        PageCommonUtils.getInstance(driver).click(dropdown, "Dropdown for " + placeholderName, "Sign In Page");
        return this;
    }

    //optionText for password is 'testingisfun99'
    public SignInPage selectOptionFromDropdown(String optionText) {
        WebElement option = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.XPATH.value(), "//div[contains(@id,'react-select') and contains(text(),'" + optionText + "')]");
        PageCommonUtils.getInstance(driver).click(option, "Dropdown option: " + optionText, "Sign In Page");
        return this;
    }

}

