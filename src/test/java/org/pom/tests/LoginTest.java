package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest{

    @Test(description = "verify valid login credentials")
    public void verifyValidLoginCredentials(){
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loadLoginPage();
        loginPage.isHeaderVisible();
        loginPage.enterTextInInputBox("username", "tomsmith");
        loginPage.enterTextInInputBox("password", "SuperSecretPassword!");
        loginPage.clickLoginButton();
        loginPage.verifyLoginSuccessful();
    }

    @Test(description = "verify invalid login credentials")
    public void verifyInvalidLoginCredentials(){
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loadLoginPage();
        loginPage.isHeaderVisible();
        loginPage.enterTextInInputBox("username", "invalidUser");
        loginPage.enterTextInInputBox("password", "invalidPassword");
        loginPage.clickLoginButton();
        loginPage.isErrorMessageDisplayed("Your username is invalid!");
    }
}
