package org.pom.utils;

import org.pom.base.*;
import org.testng.Assert;

public class AssertUtils extends BaseTest {

    private static final ThreadLocal<AssertUtils> assertUtils = ThreadLocal.withInitial(AssertUtils::new);

    String textAssertionPassed = "Assertion passed - ";
    String textAssertionFailed = "Assertion failed - ";
    String startingTag = "<b><i>";
    String closingTag = "</i></b>";
    String formattingTag = " <b>|<i> ";
    String textConditionIsReturning = "Condition is returning ";
    String security = "Security";

    public static AssertUtils getInstance() {
        return assertUtils.get();
    }

    private void assertEquals(Object actual, Object expected, boolean isSoftAssert) {
        try {
            Assert.assertEquals(actual, expected);
            System.out.println(textAssertionPassed + startingTag + "Actual Text: " + closingTag + actual + " and " + startingTag + " Expected Text: " + closingTag + expected + ".");
        } catch (AssertionError assertionError) {
            String reportStep = textAssertionFailed + startingTag + "Actual Text: " + closingTag + actual + " and " + startingTag + " Expected Text: " + closingTag + expected + ".";
            reportAssertionStep(isSoftAssert, reportStep, assertionError);
        }
    }

    public void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, false);
    }

    public void softAssertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, true);
    }

    private void assertTrueOrFalse(boolean condition, boolean isAssertTrue, boolean isSoftAssert, String message) {
        try {
            if (isAssertTrue) Assert.assertTrue(condition);
            else Assert.assertFalse(condition);
            if (!message.equalsIgnoreCase("")) {
                System.out.println(textAssertionPassed + startingTag + message + closingTag + ".");
            } else {
                System.out.println(textAssertionPassed + startingTag + textConditionIsReturning + isAssertTrue + closingTag + ".");
            }
        } catch (AssertionError assertionError) {
            String reportStep = textAssertionFailed + startingTag + message + " " + textConditionIsReturning + !isAssertTrue + closingTag + ".";
            reportAssertionStep(isSoftAssert, reportStep, assertionError);
        }
    }

    public void assertTrue(boolean condition) {
        assertTrueOrFalse(condition, true, false, "");
    }

    public void assertTrue(boolean condition, String message) {
        assertTrueOrFalse(condition, true, false, message);
    }

    public void softAssertTrue(boolean condition) {
        assertTrueOrFalse(condition, true, true, "");
    }

    public void softAssertTrue(boolean condition, String message) {
        assertTrueOrFalse(condition, true, true, message);
    }

    public void assertFalse(boolean condition) {
        assertTrueOrFalse(condition, false, false, "");
    }

    public void assertFalse(boolean condition, String message) {
        assertTrueOrFalse(condition, false, false, message);
    }

    public void softAssertFalse(boolean condition) {
        assertTrueOrFalse(condition, false, true, "");
    }

    public void softAssertFalse(boolean condition, String message) {
        assertTrueOrFalse(condition, false, true, message);
    }

    private void reportAssertionStep(boolean isSoftAssert, String reportStep, AssertionError assertionError) {
        if (isSoftAssert) {
            System.out.println(reportStep + " [SOFT ASSERT WARNING]");
            setIsFail(Boolean.TRUE);
            addException(assertionError);
        } else {
            System.out.println(reportStep + " [HARD ASSERT FAIL]");
            throw assertionError;
        }
    }

    public void assertTrueOrFalseWithDescription(boolean assertCondition, String assertConditionDescription, boolean isAssertTrue) {
        try {
            if (isAssertTrue) Assert.assertTrue(assertCondition);
            else Assert.assertFalse(assertCondition);
            System.out.println(textAssertionPassed + assertConditionDescription + formattingTag + textConditionIsReturning + isAssertTrue + closingTag + ".");
        } catch (AssertionError assertionError) {
            String reportStep = textAssertionFailed + assertConditionDescription + formattingTag + textConditionIsReturning + !isAssertTrue + closingTag + ".";
            System.out.println(reportStep + " [HARD ASSERT FAIL]");
            throw assertionError;
        }
    }
}
