package org.pom.factory.abstractfactory;
import org.pom.enums.BrowserEnum;

public class DriverManagerFactoryAbstract {

    protected DriverManagerFactoryAbstract() {
    }

    public static DriverManagerAbstract getManager(BrowserEnum browserEnum) {
        System.out.println("Entered start get manager method");
        switch (browserEnum) {
            case CHROME -> {
                return new ChromeDriverManagerAbstract();
            }
            case FIREFOX -> {
                return new FirefoxDriverManagerAbstract();
            }
            default -> throw new IllegalStateException("Unexpected value: " + browserEnum);
        }
    }
}
