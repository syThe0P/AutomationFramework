package org.pom.utils;

import org.pom.base.BaseTest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class PropertyLoader extends BaseTest {

    public static PropertyLoader getInstance() {
        return new PropertyLoader();
    }

    public synchronized Properties propertyLoader(String filePath) {
        Properties properties = new Properties();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                logger.log(Level.INFO,String.format("Error occurred while loading properties file: %s", e));
                throw new RuntimeException("failed to load properties file " + filePath);
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.INFO,String.format("Error occurred while reading properties files:%s", e));
            throw new RuntimeException("properties file not found at " + filePath);
        }
        return properties;
    }
}
