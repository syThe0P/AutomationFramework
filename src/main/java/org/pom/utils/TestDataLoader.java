//package org.pom.utils;
//
//import org.pom.enums.ConfigEnum;
//import org.pom.enums.DirectoryEnum;
//
//import java.util.Properties;
//
//public class TestDataLoader {
//    private final String environment = ConfigEnum.valueOf(ConfigEnum.ENVIRONMENT);
//    private static final String DIRECTORY_SEPARATOR = DirectoryEnum.valueOf(DirectoryEnum.DIRECTORY_SEPARATOR);
//
//    public synchronized Properties getTestDataProperties(String module) {
//        String testDataFilePath = DirectoryEnum.valueOf(DirectoryEnum.TEST_DATA_DIRECTORY_PATH) + module + DIRECTORY_SEPARATOR + environment.toLowerCase() + "_testData_" + module + ".properties";
//        return PropertyLoader.getInstance().propertyLoader(testDataFilePath);
//    }
//
//    public static TestDataLoader getInstance() {
//        return new TestDataLoader();
//    }
//
//    public static String getTestingFilePath(String testClassPath) {
//        String moduleName = testClassPath.split("org.pom.tests.")[1].split("\\.")[0];
//        return DirectoryEnum.valueOf(DirectoryEnum.TESTING_FILES_DIRECTORY_PATH) + moduleName + DIRECTORY_SEPARATOR;
//    }
//
//    public static String getPageFilePath(String testClassPath) {
//        String moduleName = testClassPath.split("org.pom.pages.")[1].split("\\.")[0];
//        return DirectoryEnum.valueOf(DirectoryEnum.TESTING_FILES_DIRECTORY_PATH) + moduleName + DIRECTORY_SEPARATOR;
//    }
//
//    public synchronized String getModuleProperty(String key) {
//        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//        String callerClassName = null;
//        for (int i = 2; i < stackTrace.length; i++) {
//            if (stackTrace[i].getClassName().contains("org.pom.tests")) {
//                callerClassName = stackTrace[i].getClassName();
//                break;
//            }
//        }
//        if (callerClassName != null) {
//            String module = callerClassName.split("org.pom.tests.")[1].split("\\.")[0];
//            return getTestDataProperties(module).getProperty(key);
//        } else {
//            return null;
//        }
//    }
//}
