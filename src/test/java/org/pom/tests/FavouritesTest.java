package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.pages.CheckOutSideModal;
import org.pom.pages.DemoPage;
import org.pom.pages.FavouritesPage;
import org.pom.pages.SignInPage;
import org.testng.annotations.Test;

public class FavouritesTest extends BaseTest {

    @Test(description = "Login, favourite iPhone 12, and verify in Favourites tab")
    public void verifyFavouriteFunctionalityForIphone12() {
        SignInPage signInPage = new SignInPage(getDriver());
        signInPage.loadSignInPage();
        signInPage.clickOnUserNameOrPasswordDropdown("username", "Select Username");
        signInPage.selectOptionFromDropdown("demouser");
        signInPage.clickOnUserNameOrPasswordDropdown("password", "Select Password");
        signInPage.selectOptionFromDropdown("testingisfun99");
        DemoPage demoPage = signInPage.clickOnLogInButton();
        demoPage.addItemToFavourites("iPhone 12");
        FavouritesPage favouritesPage = new FavouritesPage(getDriver());
        favouritesPage.load();
        assert favouritesPage.isFavouriteButtonOnProductClicked("iPhone 12") : "iPhone 12 is not present in Favourites!";
    }
}
