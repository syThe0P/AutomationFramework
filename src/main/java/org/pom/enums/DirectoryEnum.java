//package org.pom.enums;
//
//import org.pom.utils.DirectoryPathLoader;
//
//public enum DirectoryEnum {
//    DIRECTORY_SEPARATOR(directoryPathLoaderInstance().get("DIRECTORY_SEPARATOR")),
//    USER_DIRECTORY(directoryPathLoaderInstance().get("USER_DIRECTORY")),
//    DOWNLOAD_PATH(directoryPathLoaderInstance().get("DOWNLOAD_PATH")),
//    REPORTS_DIRECTORY_PATH(directoryPathLoaderInstance().get("REPORTS_DIRECTORY_PATH")),
//    ALLURE_REPORTS_DIRECTORY_PATH(directoryPathLoaderInstance().get("ALLURE_REPORTS_DIRECTORY_PATH")),
//    TEMPLATES_DIRECTORY_PATH(directoryPathLoaderInstance().get("TEMPLATES_DIRECTORY_PATH")),
//    TEST_DATA_DIRECTORY_PATH(directoryPathLoaderInstance().get("TEST_DATA_DIRECTORY_PATH")),
//    TESTING_FILES_DIRECTORY_PATH(directoryPathLoaderInstance().get("TESTING_FILES_DIRECTORY_PATH")),
//    TESTING_FILES_UTILS_DIRECTORY_PATH(directoryPathLoaderInstance().get("TESTING_FILES_UTILS_DIRECTORY_PATH"));
//    private final String value;
//
//    DirectoryEnum(String value) {
//        this.value = value;
//    }
//
//    public static String valueOf(DirectoryEnum config) {
//        for (DirectoryEnum e : values()) {
//            if (e.value.equals(config.value)) {
//                return e.value;
//            }
//        }
//        return null;
//    }
//
//    private static DirectoryPathLoader directoryPathLoaderInstance() {
//        return new DirectoryPathLoader();
//    }
//}