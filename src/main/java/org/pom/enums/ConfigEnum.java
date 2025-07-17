package org.pom.enums;

import org.pom.utils.ConfigLoader;

public enum ConfigEnum {
    PROJECT_NAME(configLoader().get("PROJECT_NAME")),
    ENVIRONMENT(configLoader().get("ENVIRONMENT")),
    BROWSER(configLoader().get("BROWSER")),
    IS_HEADLESS(configLoader().get("IS_HEADLESS")),
    WAIT_TIMEOUT(configLoader().get("WAIT_TIMEOUT")),
    URL(configLoader().get("URL"));

    private final String value;

    ConfigEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static ConfigLoader configLoader() {
        return new ConfigLoader();
    }
}
