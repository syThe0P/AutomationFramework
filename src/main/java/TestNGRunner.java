import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.*;

public class TestNGRunner {
    public static void main(String[] args) {
        // Read parameters from system properties (Jenkins passes them this way)
        String classesParam = System.getProperty("Classes", "");
        String methodsParam = System.getProperty("Methods", "");
        String browser = System.getProperty("Browser", "CHROME"); // example

        TestNG testng = new TestNG();
        XmlSuite suite = new XmlSuite();
        suite.setName("DynamicSuite");

        XmlTest test = new XmlTest(suite);
        test.setName("DynamicTest");
        test.setParameters(Map.of("browser", browser)); // Pass browser to test

        List<XmlClass> xmlClasses = new ArrayList<>();

        if (!classesParam.isEmpty()) {
            String[] classNames = classesParam.split("\\|");
            for (String fullClassName : classNames) {
                XmlClass xmlClass = new XmlClass(fullClassName.trim());

                if (!methodsParam.isEmpty()) {
                    String[] methodNames = methodsParam.split("\\|");
                    List<XmlInclude> includes = new ArrayList<>();
                    for (String method : methodNames) {
                        includes.add(new XmlInclude(method.trim()));
                    }
                    xmlClass.setIncludedMethods(includes);
                }

                xmlClasses.add(xmlClass);
            }
        }

        test.setXmlClasses(xmlClasses);
        testng.setXmlSuites(List.of(suite));
        testng.run();
    }
}
