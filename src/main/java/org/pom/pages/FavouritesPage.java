package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.base.BaseTest;
import org.pom.enums.LocatorEnum;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;

public class FavouritesPage extends BasePage {

    private static final String PAGE_NAME = "Favourites page";


    public FavouritesPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }

    public FavouritesPage load() {
        load(BaseTest.linkedHashMapMasterTestData.get("BASE_URL") + "/favourites");
        return this;
    }

    public boolean isFavouriteButtonOnProductClicked(String productName){
        return ValidationCommonUtils.getInstance(driver).isElementPresent("//button[contains(@class,'Button clicked')]/parent::div/following-sibling::div/img[@alt='" + productName + "']","Favourite button",PAGE_NAME);
    }

    public FavouritesPage removeProductFromFavourites(String productName) {
        PageCommonUtils.getInstance(driver).click("//button[contains(@class,'Button clicked')]/parent::div/following-sibling::div/img[@alt='" + productName + "']", LocatorEnum.XPATH.value(), productName + " remove from favourites button", PAGE_NAME);
        return this;
    }
}
