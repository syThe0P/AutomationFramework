package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.enums.LocatorEnum;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;

public class OrdersPage extends BasePage {
    private static final String PAGE_NAME = "Orders page";

    public OrdersPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }

    public OrdersPage loadOrdersPage() {
        load("https://testathon.live/orders"); // Replace with actual orders URL
        return this;
    }

    public boolean verifyNoOrdersMessageDisplayed() {
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//h2[contains(text(),'No orders found')]", LocatorEnum.XPATH.value(), "No orders message", PAGE_NAME);
    }

    public boolean verifyOrderPlacedForProduct(String titleName, String descripitonName, String amount) {
        WebElement product = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.XPATH.value(), "//strong[text()='Title:']/parent::div[contains(normalize-space(),'"+titleName+"')]/following-sibling::div[contains(normalize-space(),'"+descripitonName+"')]//strong[text()='Description:']/parent::div/following-sibling::div//span[contains(normalize-space(),'$"+amount+"')]/ancestor::div[@class='a-box shipment shipment-is-delivered']/preceding-sibling::div//span[text()='Order placed']");
        return ValidationCommonUtils.getInstance(driver).isDisplayed(product, "Order Placed for " + titleName + " having price: $" + amount, PAGE_NAME);
    }
}
