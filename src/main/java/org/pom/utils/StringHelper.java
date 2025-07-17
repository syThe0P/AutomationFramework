package org.pom.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.pom.base.BaseTest.logger;

public class StringHelper {
    private static StringHelper stringHelper;

    public static StringHelper getInstance() {
        if (stringHelper == null) {
            stringHelper = new StringHelper();
        }
        return stringHelper;
    }

    public String trimEnd(String stringText, String suffix) {
        if (stringText.trim().endsWith(suffix))
            return stringText.trim().substring(0, stringText.trim().length() - suffix.length());
        return stringText.trim();
    }

    public String trimStart(String stringText, String suffix) {
        if (stringText.trim().startsWith(suffix))
            return stringText.trim().substring(suffix.length(), stringText.trim().length());
        return stringText.trim();
    }

    public boolean matchSubstringFromString(String set, String subset) {
        final String REGEX = "[^a-zA-Z0-9]";
        String newSet = set.replaceAll(REGEX, "");
        String newSubset = subset.replaceAll(REGEX, "");
        return newSet.toLowerCase().trim().contains(newSubset.toLowerCase().trim());
    }

    public String removeSpecialCharacters(String stringText) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(stringText);
        return matcher.replaceAll("");
    }

    public String getNumericValue(String actualString, boolean removeComma, boolean removeDot) {
        String output = "";
        String pattenString = "[^0-9,.]";
        if (removeComma) pattenString = pattenString.replace(",", "");
        if (removeDot) pattenString = pattenString.replace(".", "");
        try {
            Pattern pattern = Pattern.compile(pattenString);
            Matcher matcher = pattern.matcher(actualString);
            output = matcher.replaceAll("");
        } catch (Exception e) {
            logger.log(Level.WARNING, "\nException occurred:- {0}", e.toString());
        }
        return output;
    }

    public String getNumericValue(String actualString) {
        return getNumericValue(actualString, false, false);
    }

    public String getResultBasedOnRegex(String actualText, String regex, int index) {
        String result = "";
        try {
            result = actualText.split(regex)[index].trim();
        } catch (Exception e) {
            LinkedHashMap<String, String> linkedHashMapTestParameters = new LinkedHashMap<>();
            linkedHashMapTestParameters.put("Actual Text", actualText);
            linkedHashMapTestParameters.put("Regex", regex);
            linkedHashMapTestParameters.put("Index", String.valueOf(index));
            System.out.println("Unable to get data based on regex due to exception - " + e.getClass().getSimpleName());
            System.out.println("Details: " + linkedHashMapTestParameters);
        }
        return result;
    }

    public String removeScriptTagsFromString(String stringWithScriptTag) {
        return stringWithScriptTag.replace("<", "&lt;").replace(">", "&gt;");
    }

    public boolean containsAllSubstrings(String response, List<String> substrings) {
        boolean flag = true;
        if (response.isEmpty() || substrings.isEmpty()) {
            System.out.println("Either response or substrings list is empty");
            return false;
        }
        for (String substring : substrings) {
            if (!response.contains(substring)) {
                System.out.println(substring + " not found in given Response");
                flag = false;
            }
        }
        return flag;
    }
}
