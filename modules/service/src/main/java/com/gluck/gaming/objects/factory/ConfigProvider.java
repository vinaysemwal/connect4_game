package com.gluck.gaming.objects.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Config Provider class to load all the properties required at runtime.
 *
 * @author Vinay Semwal
 */
public class ConfigProvider {

    private static final Logger logger = LogManager.getLogger(ConfigProvider.class);

    private static final String CONFIG_LOCATION = "/config.properties";

    final Properties configurations = new Properties();

    /**
     * Object construction to load all the properties from the config location.
     */
    public ConfigProvider() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            logger.info("Loading config from the config file : {}", CONFIG_LOCATION);
            final InputStream in = this.getClass().getResourceAsStream(CONFIG_LOCATION);
            configurations.load(in);
        } catch (final IOException e) {
            logger.fatal(
                "I/O Error occurred while trying to load config from the config file : {}. Application will not work unless the problem is fixed. Exception: ",
                CONFIG_LOCATION,
                e);
        }
    }

    /**
     * @return the configurations
     */
    public Properties getConfigurations() {
        return configurations;
    }
}