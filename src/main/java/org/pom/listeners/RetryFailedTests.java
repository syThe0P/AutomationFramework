package org.pom.listeners;

import org.pom.enums.ConfigEnum;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryFailedTests implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_TRY = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess() && count < MAX_TRY) {
            count++;
            return true;
        }
        return false;
    }
}
