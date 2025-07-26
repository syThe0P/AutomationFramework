package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;

public class OrdersPage extends BasePage {
    private static final String PAGE_NAME = "Orders page";


    public OrdersPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }


}
