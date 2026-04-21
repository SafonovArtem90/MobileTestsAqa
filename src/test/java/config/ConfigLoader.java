package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties PROPERTIES = new Properties();
    private static final String CONFIG_PROPERTY = "config.properties";

    static {
        try (InputStream resourceStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTY)) {
            PROPERTIES.load(resourceStream);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении config.properties", e);
        }
    }

    public static String getProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null) {
            throw new RuntimeException(String.format("Ключ %s не найден в %s", key, CONFIG_PROPERTY));
        }
        return value;
    }
}
