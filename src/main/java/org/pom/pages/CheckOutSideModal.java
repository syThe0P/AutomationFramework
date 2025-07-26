package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.utils.seleniumutils.PageCommonUtils;

public class CheckOutSideModal extends BasePage {

    private static final String PAGE_NAME = "CheckOut Page";

    @FindBy(xpath = "//div[@class=\"float-cart__content\"]")
    private WebElement checkOutBag;

    @FindBy(xpath = "//div[@class=\"buy-btn\"]")
    private WebElement checkOutButton;


    public CheckOutSideModal(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public CheckOutSideModal clickOnCheckoutBag() {
        PageCommonUtils.getInstance(driver).click(checkOutBag, "checkOutBag", PAGE_NAME);
        return this;
    }

    public CheckOutSideModal clickOnCheckOutButton() {
        PageCommonUtils.getInstance(driver).click(checkOutButton, "checkOutButton", PAGE_NAME);
        return this;
    }

}
