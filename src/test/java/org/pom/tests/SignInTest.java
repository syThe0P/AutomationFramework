package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.listeners.ExtentReportListeners;
import org.pom.pages.CheckOutSideModal;
import org.pom.pages.DemoPage;
import org.pom.pages.SignInPage;
import org.pom.utils.BugReporter;
import org.pom.utils.seleniumutils.WaitUtilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListeners.class)
public class SignInTest extends BaseTest {

    @AfterMethod
    public void reportBugIfTestFailed(ITestResult result) {
        BugReporter.reportIfTestFailed(result);
    }

    @Test(description = "Verify Sign to the application")
    public void verifySignInToApplication() {
        SignInPage signInPage = new SignInPage(getDriver());
        DemoPage demoPage = new DemoPage(getDriver());
        signInPage.loadSignInPage();
        signInPage.isHeaderImageVisible();
        signInPage.clickOnUserNameOrPasswordDropdown("username", "Select Username");
        signInPage.selectOptionFromDropdown("demouser");
        signInPage.clickOnUserNameOrPasswordDropdown("password", "Select Password");
        signInPage.selectOptionFromDropdown("testingisfun99");
        signInPage.clickOnLogInButton();
        demoPage.areHeadersVisible();
        demoPage.isLogoutOrSignInButtonDisplayed("logout");
    }

    @Test(description = "Verify Sign in with invalid credentials")
    public void verifySignInWithInvalidCredentials() {
        SignInPage signInPage = new SignInPage(getDriver());
        signInPage.loadSignInPage();
        signInPage.isHeaderImageVisible();
        signInPage.clickOnLogInButton();
        signInPage.isErrorMessageDisplayed("Invalid Username");
        signInPage.loadSignInPage();
        signInPage.clickOnUserNameOrPasswordDropdown("username", "Select Username");
        signInPage.selectOptionFromDropdown("locked_user");
        signInPage.clickOnUserNameOrPasswordDropdown("password", "Select Password");
        signInPage.selectOptionFromDropdown("testingisfun99");
        signInPage.clickOnLogInButton();
        signInPage.isErrorMessageDisplayed("Your account has been locked.");
    }
}
