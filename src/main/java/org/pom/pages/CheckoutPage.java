package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.base.BaseTest;
import org.pom.enums.LocatorEnum;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;

public class CheckoutPage extends BasePage {

    private static final String PAGE_NAME = "Checkout page";


    public CheckoutPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }

    public CheckoutPage load() {
        load(BaseTest.linkedHashMapMasterTestData.get("BASE_URL") + "/checkout");
        return this;
    }

    public boolean isAddressHeaderDisplayed(){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//legend[@data-test='shipping-address-heading']",LocatorEnum.XPATH.value(), "Address header", PAGE_NAME);
    }

    public CheckoutPage enterTextInAddressField(String inputBoxId, String text) {
        WebElement inputBox = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.ID.value(), inputBoxId);
        PageCommonUtils.getInstance(driver).clearAndSendKeys(inputBox, inputBoxId, text);
        return this;
    }

    public CheckoutPage clickOnSubmitButton(){
        PageCommonUtils.getInstance(driver).click("checkout-shipping-continue",LocatorEnum.ID.value(), "Submit button", PAGE_NAME);
        return this;
    }

    public boolean isProductWithDetailsDisplayedUnderOrderSummary(String productName, String productAmount){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//h5[text()='" + productAmount + "']/parent::div/following-sibling::div/div[normalize-space()='" + productAmount + "']", LocatorEnum.XPATH.value(), productName + " with amount " + productAmount, PAGE_NAME);
    }

    public boolean isCartItemsTotalPriceDisplayed(String totalAmount){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//span[@class='cart-priceItem-value']/span[normalize-space()='" + totalAmount + "']", LocatorEnum.XPATH.value(), "Cart items total price" + totalAmount, PAGE_NAME);
    }
}
