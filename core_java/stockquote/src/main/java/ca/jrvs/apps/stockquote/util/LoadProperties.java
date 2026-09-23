package ca.jrvs.apps.stockquote.util;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadProperties {

  private static final Logger logger = LoggerFactory.getLogger(LoadProperties.class);

  /**
   * Gets a map of necessary environment variables for use in the app
   *
   * @return map of env variables
   */
  public static Map<String, String> loadProperties() {
    logger.debug("Reading properties file");
    Map<String, String> properties = new HashMap<>();

    properties.put("api-key", System.getenv("api-key"));
    properties.put("db-host", System.getenv("db-host"));
    properties.put("db-name", System.getenv("db-name"));
    properties.put("db-port", System.getenv("db-port"));
    properties.put("db-user", System.getenv("db-user"));
    properties.put("db-password", System.getenv("db-password"));

    boolean ready = true;
    for (Map.Entry<String, String> property : properties.entrySet()) {
      if (property.getValue() == null) {
        logger.error("null property of {}", property.getKey());
        ready = false;
      }
    }

    if (!ready) {
      logger.error("Environment variables not set");
      throw new RuntimeException("Environment variables not set");
    }

    return properties;
  }
}
