package org.pom.utils;

import com.github.javafaker.Faker;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RandomGenerator {

    Faker faker = new Faker();

    public static RandomGenerator getInstance() {
        return new RandomGenerator();
    }

    public Long generateRandomNumber(int length) {
        return faker.number().randomNumber(length, true);
    }

    public String generateRandomString(int stringLength) {
        SecureRandom random = new SecureRandom();
        StringBuilder buffer = new StringBuilder(stringLength);
        for (int i = 0; i < stringLength; i++) {
            char temp = (char) ('a' + random.nextInt('z' - 'a'));
            buffer.append(temp);
        }
        return buffer.toString();
    }

    public String generateRandomEmail(String jiraId) {
        jiraId = (jiraId.trim().equalsIgnoreCase("")) ? "delete_it_" : jiraId.trim() + "_delete_it_";
        return jiraId + generateRandomString(5) + "@brevo.com";
    }

    public String generateRandomEmail() {
        return generateRandomEmail("");
    }

    public String generateRandomSms() {
        return "9198" + generateRandomNumber(8);
    }


    public String generateRandomSmsWithoutCountryCode() {
        return "989" + generateRandomNumber(7);
    }

    public List<String> generateRandomEmails(int numberOfEmails, String jiraId) {
        List<String> listEmails = new ArrayList<>();
        for (int i = 0; i < numberOfEmails; i++)
            listEmails.add(generateRandomEmail(jiraId));
        return listEmails;
    }

    public List<String> generateRandomExtIds(int numberOfExtIds, String jiraId) {
        List<String> listExtIds = new ArrayList<>();
        for (int i = 0; i < numberOfExtIds; i++)
            listExtIds.add("Delete_Ext_Id " + jiraId + RandomGenerator.getInstance().generateRandomNumber(5));
        return listExtIds;
    }

    public List<String> generateRandomEmails(int numberOfEmails) {
        return generateRandomEmails(numberOfEmails, "");
    }

    public String generateRandomSpecialCharacters(int stringLength) {
        StringBuilder buffer = new StringBuilder(stringLength);
        String specialCharacters = "!@#$%^&*())_+|}{:?><~,./=";
        for (int i = 0; i < stringLength; i++) {
            int index = new SecureRandom().nextInt(specialCharacters.length());
            char randomChar = specialCharacters.charAt(index);
            buffer.append(randomChar);
        }
        return buffer.toString();
    }

    public String generateRandomParagraph(int sentenceCount) {
        return faker.lorem().paragraph(sentenceCount);
    }

    public String generateRandomTextBasedOnCase(int length, boolean includeUppercase, boolean includeDigit){
        return faker.lorem().characters(length,includeUppercase,includeDigit);
    }

    public String generateRandomAlphaNumericText(int length){
        return generateRandomTextBasedOnCase(length,true,true);
    }

    public String generateRandomStringBasedOnLocale(String locale,int length){
       return new Faker(new Locale(locale)).lorem().fixedString(length).replace(" ","");
    }
}