package org.pom.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.base.BaseTest;
import org.pom.enums.LocatorEnum;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;

public class DemoPage extends BasePage {

    private static final String PAGE_NAME = "Demo page";


    @FindBy(xpath = "//p[normalize-space()=\"iPhone 12\"]/following-sibling::div[text()='Add to cart']")
    private WebElement addToCartButton;

    @FindBy(xpath = "//div[contains(@class, 'shelf-item__thumb')]//img")
    private WebElement numberOfItemsOnPage;



    public DemoPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public DemoPage load() {
        load("https://testathon.live/");
        areHeadersVisible();
        return this;
    }

    public boolean areHeadersVisible() {
        String xpathTemplate = "//strong[normalize-space()=\"%s\"]";
        String[] headers = {"Offers", "Orders", "Favourites"};
        for (String header : headers) {
            String xpath = String.format(xpathTemplate, header);
            try {
                if (!driver.findElement(By.xpath(xpath)).isDisplayed()) {
                    return false;
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                return false;
            }
        }
        return true;
    }

    public DemoPage clickOnVersionDropdownOnClickedLinks(String productName) {
        PageCommonUtils.getInstance(driver).click("//p[normalize-space()=\"" + productName + "\"]/following-sibling::div[text()='Add to cart']", LocatorEnum.XPATH.value(), productName + " add to cart button", PAGE_NAME);
        return this;
    }

    // Apple, Samsung, OnePlus,Google
    public void selectVendor(String vendorName) {
        PageCommonUtils.getInstance(driver).click("//span[normalize-space()=\"" + vendorName + "\"]", LocatorEnum.XPATH.value(), vendorName , PAGE_NAME);
    }

    public int getProductCount() {
        return driver.findElements(By.xpath("//div[contains(@class, 'shelf-item__thumb')]//img")).size();
    }

    public boolean isProductCountLineOnTopOfPageDisplayingCorrectNumber() {
        int count = getProductCount();
        String xpath = "//span[normalize-space()=\"" + count + " Product(s) found.\"]";
        return !driver.findElements(By.xpath(xpath)).isEmpty();
    }

    public DemoPage addItemToFavourites(String productName) {
        String xpath = "//p[normalize-space()=\"" + productName + "\"]/ancestor::div[contains(@class,'shelf-item')]//button[contains(@class, 'MuiButtonBase-root') and contains(@class, 'Button')]";
        PageCommonUtils.getInstance(driver).click(xpath, LocatorEnum.XPATH.value(), productName + " add to favourites button", PAGE_NAME);
        return this;
    }

    public boolean isFavouriteHeartYellow(String productName) {
        String xpath = "//p[normalize-space()=\"" + productName + "\"]/ancestor::div[contains(@class,'shelf-item')]//button[contains(@class, 'MuiButtonBase-root') and contains(@class, 'MuiIconButton-root') and contains(@class, 'Button') and contains(@class, 'clicked')]";
        return !driver.findElements(By.xpath(xpath)).isEmpty();
    }

    public DemoPage clickOnAddToCart(String productName) {
        String xpath = "//p[normalize-space()=\"" + productName + "\"]/following-sibling::div[contains(text(), 'Add to cart')]";
        PageCommonUtils.getInstance(driver).click(xpath, LocatorEnum.XPATH.value(), productName + " add to cart button", PAGE_NAME);
        return this;
    }

    public DemoPage clickOnAddToFavouritesButton(String productName){
        PageCommonUtils.getInstance(driver).click("//img[@alt='"+productName+"']/parent::div/preceding-sibling::div//button[contains(@class,'Button clicked')]", LocatorEnum.XPATH.value(), productName + " remove from favourites button", PAGE_NAME);
        return this;
    }

    public boolean isLogoutOrSignInButtonDisplayed(String buttonId){
        WebElement button = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.ID.value(), buttonId);
        return ValidationCommonUtils.getInstance(driver).isDisplayed(button, "button " + buttonId , PAGE_NAME);
    }


}
