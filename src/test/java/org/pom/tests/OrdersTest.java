package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.pages.SignInPage;
import org.pom.pages.OrdersPage;
import org.testng.annotations.Test;

public class OrdersTest extends BaseTest {
    @Test(description = "Verify 'No orders found' message is displayed when there are no orders")
    public void verifyNoOrdersFoundMessage() {
        SignInPage signInPage = new SignInPage(getDriver());
        signInPage.loadSignInPage();
        signInPage.clickOnUserNameOrPasswordDropdown("username", "Select Username");
        signInPage.selectOptionFromDropdown("demouser");
        signInPage.clickOnUserNameOrPasswordDropdown("password", "Select Password");
        signInPage.selectOptionFromDropdown("testingisfun99");
        signInPage.clickOnLogInButton();
        OrdersPage ordersPage = new OrdersPage(getDriver());
        ordersPage.loadOrdersPage();
        assert ordersPage.verifyNoOrdersMessageDisplayed() : "'No orders found' message is not displayed!";
    }


}