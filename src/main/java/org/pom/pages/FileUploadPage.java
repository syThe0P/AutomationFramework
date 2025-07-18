package org.pom.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.pom.base.BasePage;
import org.pom.enums.LocatorEnum;
import org.pom.utils.ConfigLoader;
import org.pom.utils.seleniumutils.FileUploadUtils;
import org.pom.utils.seleniumutils.PageCommonUtils;
import org.pom.utils.seleniumutils.ValidationCommonUtils;

public class FileUploadPage extends BasePage {

    @FindBy(xpath = "//h3[contains(text(),'Dropdown List')]")
    private WebElement heading;

    public FileUploadPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);
    }

    public FileUploadPage loadFileUploadPage(){
        load(ConfigLoader.get("BASE_URL") +  "/upload");
        return this;
    }

    public FileUploadPage uploadImageFile(String fileName) {
        WebElement uploadInput = PageCommonUtils.getInstance(driver).createWebElementByLocator(LocatorEnum.ID.value(), "file-upload");
        FileUploadUtils.getInstance(driver).uploadFile(uploadInput,fileName);
        return this;
    }

    public FileUploadPage clickOnUploadButton(){
        PageCommonUtils.getInstance(driver).click("file-submit", LocatorEnum.ID.value(), "Upload Button", "File Upload Page");
        return this;
    }

    public boolean verifyFileUploadMessage(){
        return ValidationCommonUtils.getInstance(driver).isDisplayed("//h3[contains(text(),'File Uploaded!')]", LocatorEnum.XPATH.value(), "File Uploaded Message", "File Upload Page");
    }
}
