package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.base.BaseTest;
import org.pom.enums.LocatorEnum;
import org.pom.utils.seleniumutils.ValidationCommonUtils;

public class OffersPage extends BasePage {
    private static final String PAGE_NAME = "Offers page";


    public OffersPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }

    public OffersPage load() {
        load(BaseTest.linkedHashMapMasterTestData.get("BASE_URL") + "/offers");
        return this;
    }

    public boolean isOffersPageHeadingDisplayed(){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//div[contains(text(),\"We've promotional offers in your location.\")]", LocatorEnum.XPATH.value(), "Offers page heading", PAGE_NAME);
    }

    //for example: productType = "iphone", offerType = "30% off on iphone"
    public boolean isProductWithOfferDisplayed(String productType,String offerType){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//div[@id='" + productType + "']/div[text()='" + offerType + "']", LocatorEnum.XPATH.value(), productType + " with " + offerType + " offer", PAGE_NAME);
    }

    public boolean isProductOrOfferImageDisplayed(String imageAlt){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//img[@alt='" + imageAlt + "']", LocatorEnum.XPATH.value(), "Offer image of type: " + imageAlt, PAGE_NAME);
    }
}
