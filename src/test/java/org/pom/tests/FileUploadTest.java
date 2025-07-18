package org.pom.tests;

import org.pom.base.BaseTest;
import org.pom.pages.FileUploadPage;
import org.testng.annotations.Test;

public class FileUploadTest extends BaseTest{

    @Test(description = "Verify file upload functionality")
    public void verifyFileUpload(){
        FileUploadPage fileUploadPage = new FileUploadPage(getDriver());
        fileUploadPage.loadFileUploadPage();
        fileUploadPage.uploadImageFile("BREVO_IMAGE.png");
        fileUploadPage.clickOnUploadButton();
        fileUploadPage.verifyFileUploadMessage();
    }
}
