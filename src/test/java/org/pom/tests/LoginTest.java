package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.utils.AssertUtils;
import org.pom.utils.BugReporter;
import org.pom.utils.seleniumutils.WaitUtilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.pom.listeners.ExtentReportListeners;

@Listeners(ExtentReportListeners.class)
public class LoginTest extends BaseTest{


    @AfterMethod
    public void reportBugIfTestFailed(ITestResult result) {
        BugReporter.reportIfTestFailed(result);
    }


    @Test(description = "verify valid login credentials")
    public void verifyValidLoginCredentials(){
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loadLoginPage();
        loginPage.isHeaderVisible();
        loginPage.enterTextInInputBox("username", "tomsmith");
        loginPage.enterTextInInputBox("password", "SuperSecretPassword!");
        WaitUtilities.getInstance(getDriver()).applyStaticWait(2);
        loginPage.clickLoginButton();
        //loginPage.verifyLoginSuccessful();
    }


    @Test(description = "verify invalid login credentials")
    public void verifyInvalidLoginCredentials(){
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loadLoginPage();
        loginPage.isHeaderVisible();
        loginPage.enterTextInInputBox("username", "invalidUser");
        loginPage.enterTextInInputBox("password", "invalidPassword");
        loginPage.clickLoginButton();
        WaitUtilities.getInstance(getDriver()).applyStaticWait(2);
        loginPage.isErrorMessageDisplayed("Your username is invalid!");
    }

    @Test(description = "google load", groups = "jira")
    public void verifyBugCreationAndAssertion() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loadLoginPage();
        loginPage.isHeaderVisible();
        loginPage.enterTextInInputBox("username", "invalidUser");
        loginPage.enterTextInInputBox("password", "invalidPassword");
        loginPage.clickLoginButton();
        loginPage.isErrorMessageDisplayed("Your username is invalid!");
        WaitUtilities.getInstance(getDriver()).applyStaticWait(2);
        AssertUtils.getInstance().assertFalse(true, "Forcing failure to test Jira bug creation");
    }

    @Test
    public void testParallelExecution() {
        System.out.println("[" + Thread.currentThread().getName() + "] LoginTest running");
    }

}
