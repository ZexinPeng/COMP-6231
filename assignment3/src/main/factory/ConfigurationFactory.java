package main.factory;

import util.Configuration;

public class ConfigurationFactory {
    private static Configuration configuration;

    public static Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }
}
