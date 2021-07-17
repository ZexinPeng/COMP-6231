package main.factory;

import main.util.Configuration;

public class ConfigurationFactory {
    private static Configuration configuration;

    public static Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }
}
