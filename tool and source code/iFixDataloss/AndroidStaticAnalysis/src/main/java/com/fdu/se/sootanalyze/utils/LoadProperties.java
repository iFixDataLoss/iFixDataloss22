package com.fdu.se.sootanalyze.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class LoadProperties {
    private static Properties properties;

    private LoadProperties() {
    }

    public static Properties getInstance() {
        if (properties != null)
            return properties;
        try {
            properties = new Properties();
            properties.load(LoadProperties.class.getResourceAsStream("/config.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String get(String key){
        if(properties != null)
            return properties.getProperty(key);
        else
            return getInstance().getProperty(key);
    }
}
