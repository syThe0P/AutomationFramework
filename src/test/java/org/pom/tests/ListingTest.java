package org.pom.tests;

import org.pom.pages.DemoPage;
import org.pom.pages.SignInPage;
import org.testng.annotations.Test;
import org.pom.base.BaseTest;


public class ListingTest extends BaseTest {
    @Test(description = "Verify product count line on top of page displays correct number")
    public void verifyProductCountLineDisplaysCorrectNumber() {
        SignInPage signInPage = new SignInPage(getDriver());
        signInPage.loadSignInPage();
        signInPage.clickOnUserNameOrPasswordDropdown("username", "Select Username");
        signInPage.selectOptionFromDropdown("demouser");
        signInPage.clickOnUserNameOrPasswordDropdown("password", "Select Password");
        signInPage.selectOptionFromDropdown("testingisfun99");
        DemoPage demoPage = signInPage.clickOnLogInButton();
        assert demoPage.isProductCountLineOnTopOfPageDisplayingCorrectNumber() : "Product count line does not display correct number!";
    }
}