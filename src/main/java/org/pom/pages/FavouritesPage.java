package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;

public class FavouritesPage extends BasePage {

    private static final String PAGE_NAME = "Favourites page";


    public FavouritesPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }
}
