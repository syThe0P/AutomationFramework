package org.pom.utils;

public class OSInfoUtils {

    private static OSInfoUtils osInfoUtils;

    public static OSInfoUtils getInstance() {
        if (osInfoUtils == null) osInfoUtils = new OSInfoUtils();
        return osInfoUtils;
    }

    public String getOSInfo() {
        return System.getProperty("os.name").replace(" ", "_");
    }
}
