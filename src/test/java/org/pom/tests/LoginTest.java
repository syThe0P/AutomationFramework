package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.pages.LoginPage;
import org.pom.utils.AssertUtils;
import org.pom.utils.BugReporter;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest{

    @AfterMethod
    public void reportBugIfTestFailed(ITestResult result) {
        BugReporter.reportIfTestFailed(result);
    }

    @Test(description = "google load")
    public void verifyValidLoginCredentials(){
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loadLoginPage();
        loginPage.isHeaderVisible();
        loginPage.enterTextInInputBox("username", "tomsmith");
        loginPage.enterTextInInputBox("password", "SuperSecretPassword!");
        loginPage.clickLoginButton();
        loginPage.verifyLoginSuccessful();
    }

}
