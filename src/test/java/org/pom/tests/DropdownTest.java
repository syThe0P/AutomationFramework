package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.pages.DropdownPage;
import org.pom.utils.seleniumutils.WaitUtilities;
import org.testng.annotations.Test;

public class DropdownTest extends BaseTest{

    @Test(description = "verify dropdown functionality")
    public void verifyDropdownFunctionality(){
        DropdownPage dropdownPage = new DropdownPage(getDriver());
        System.out.println("Dropdown Test Started");
        dropdownPage.loadDropdownPage();
        System.out.println("Dropdown Page Loaded");
        dropdownPage.isHeaderVisible();
        System.out.println("Dropdown Page Header is Visible");
        dropdownPage.clickOnDropdown();
        System.out.println("Dropdown Clicked");
        dropdownPage.selectOptionFromDropdown("Option 1");
        System.out.println("Option 1 Selected from Dropdown");
        WaitUtilities.getInstance(getDriver()).applyStaticWait(2);
        System.out.println("Dropdown Test Completed Successfully");
    }
}
