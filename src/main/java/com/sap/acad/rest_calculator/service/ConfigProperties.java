package com.sap.acad.rest_calculator.service;

import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {
    private Properties properties;

    public ConfigProperties() {
        this.properties = new Properties();
        String pathToConfig = "config.properties";
        loadPropertiesFromFile(pathToConfig);
    }

    private void loadPropertiesFromFile(String pathToConfig) {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(pathToConfig);
            this.properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPropertyWithKey(String key) {
        return this.properties.getProperty(key);
    }
}
