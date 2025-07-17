package org.pom.utils;

public class DirectoryPathLoader {
    private final String dirSeparator = getDirectorySeparator();
    private final String userDirectory = System.getProperty("user.dir");
    private static final String RESOURCES = "resources";
    private static final String SRC = "src";

    private String getDirectorySeparator() {
        String directorySeparator;
        if (System.getProperty("os.name").toLowerCase().trim().contains("windows")) directorySeparator = "\\";
        else directorySeparator = "/";
        return directorySeparator;
    }

    public String get(String key) {
        String value = "";
        switch (key) {
            case "USER_DIRECTORY" -> value = userDirectory;
            case "DIRECTORY_SEPARATOR" -> value = dirSeparator;
            case "DOWNLOAD_PATH" -> value = userDirectory + dirSeparator + "downloadedFiles" + dirSeparator;
            case "REPORTS_DIRECTORY_PATH" -> value = userDirectory + dirSeparator + "reports" + dirSeparator;
            case "ALLURE_REPORTS_DIRECTORY_PATH" ->
                    value = userDirectory + dirSeparator + "allure-results" + dirSeparator;
            case "TEMPLATES_DIRECTORY_PATH" ->
                    value = userDirectory + dirSeparator + SRC + dirSeparator + "main" + dirSeparator + RESOURCES + dirSeparator + "templates" + dirSeparator;
            case "TEST_DATA_DIRECTORY_PATH" ->
                    value = userDirectory + dirSeparator + SRC + dirSeparator + "test" + dirSeparator + RESOURCES + dirSeparator + "testData" + dirSeparator;
            case "TESTING_FILES_DIRECTORY_PATH" ->
                    value = userDirectory + dirSeparator + SRC + dirSeparator + "test" + dirSeparator + RESOURCES + dirSeparator + "testingFiles" + dirSeparator;
            case "TESTING_FILES_UTILS_DIRECTORY_PATH" ->
                    value = userDirectory + dirSeparator + SRC + dirSeparator + "test" + dirSeparator + RESOURCES + dirSeparator + "testingFiles" + dirSeparator + "utils" + dirSeparator;
            default -> value = "Invalid key";
        }
        return value;
    }
}

