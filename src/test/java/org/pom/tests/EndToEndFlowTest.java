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
public class EndToEndFlowTest extends BaseTest {

    @AfterMethod
    public void reportBugIfTestFailed(ITestResult result) {
        BugReporter.reportIfTestFailed(result);
    }

    @Test(description = "verify end to end happy flow for buying a product")
    public void verifyValidLoginCredentials(){
        DemoPage demoPage = new DemoPage(getDriver());
        demoPage.load();
        demoPage.selectVendor("Apple");
        demoPage.clickOnAddToCart("iPhone 12");
        CheckOutSideModal checkOutSideModal = new CheckOutSideModal(getDriver());
        checkOutSideModal.clickOnCheckoutBag();
        checkOutSideModal.clickOnCheckOutButton();
        WaitUtilities.getInstance(getDriver()).applyStaticWait(2);

    }
}
