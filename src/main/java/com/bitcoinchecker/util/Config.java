package com.bitcoinchecker.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties properties;

    private Config() {
        properties = new Properties();
        loadProperties();
    }

    private static class InstanceHolder {
        private static final Config INSTANCE = new Config();
    }

    public static Config getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }

    public String getApiKey() {
        return properties.getProperty("api.key");
    }

    public String getApiBaseUrl() {
        return properties.getProperty("api.base.url");
    }

    public int getApiTimeoutSeconds() {
        return Integer.parseInt(properties.getProperty("api.timeout.seconds", "10"));
    }
}