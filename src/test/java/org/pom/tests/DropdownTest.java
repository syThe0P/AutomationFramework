package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.listeners.ExtentReportListeners;
import org.pom.pages.DropdownPage;
import org.pom.utils.seleniumutils.WaitUtilities;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListeners.class)
public class DropdownTest extends BaseTest{

    @Test(description = "verify dropdown functionality")
    public void verifyDropdownFunctionality(){
        DropdownPage dropdownPage = new DropdownPage(getDriver());
        dropdownPage.loadDropdownPage();
        dropdownPage.isHeaderVisible();
        dropdownPage.clickOnDropdown();
        dropdownPage.selectOptionFromDropdown("Option 1");
        WaitUtilities.getInstance(getDriver()).applyStaticWait(2);
    }

}
