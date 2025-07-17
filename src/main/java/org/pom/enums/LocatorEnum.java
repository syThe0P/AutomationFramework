package org.pom.enums;

public enum LocatorEnum {
    XPATH,
    ID,
    CSS;


    public String value() {
        return name().toLowerCase();
    }
}