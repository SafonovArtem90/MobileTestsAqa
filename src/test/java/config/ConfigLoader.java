package config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * Читает настройки в порядке приоритета (от высшего к низшему):
 * 1. системное свойство JVM (-Dapp.package=...);
 * 2. переменная окружения (APP_PACKAGE=...);
 * 3. config-{ENV}.properties (ENV задаётся переменной окружения, по умолчанию local);
 * 4. config.properties (значения по умолчанию).
 */
@Slf4j
public final class ConfigLoader {

    private static final String BASE_CONFIG = "config.properties";
    private static final String ENV_CONFIG_TEMPLATE = "config-%s.properties";
    private static final String ENV_KEY = "env";
    private static final String DEFAULT_ENV = "local";

    private static final Properties PROPERTIES = new Properties();

    static {
        loadRequired(BASE_CONFIG);
        String env = getFromEnvironment(ENV_KEY).orElse(DEFAULT_ENV);
        loadOptional(String.format(ENV_CONFIG_TEMPLATE, env));
        log.info("Активное окружение: {}", env);
    }

    private ConfigLoader() {
    }

    public static String getProperty(String key) {
        return getOptionalProperty(key)
                .orElseThrow(() -> new IllegalStateException(String.format(
                        "Настройка '%s' не задана: укажите -D%s, переменную окружения %s или значение в config-файле",
                        key, key, toEnvName(key))));
    }

    public static Optional<String> getOptionalProperty(String key) {
        Optional<String> external = getFromEnvironment(key);
        if (external.isPresent()) {
            return external;
        }
        return Optional.ofNullable(PROPERTIES.getProperty(key))
                       .map(String::trim)
                       .filter(value -> !value.isEmpty());
    }

    public static int getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    /**
     * Ищет значение только во внешних источниках: системных свойствах и переменных окружения.
     * Config-файлы не учитываются.
     */
    public static Optional<String> getFromEnvironment(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(toEnvName(key));
        }
        return isBlank(value) ? Optional.empty() : Optional.of(value.trim());
    }

    /**
     * Преобразует ключ в имя переменной окружения: app.path -> APP_PATH.
     */
    public static String toEnvName(String key) {
        return key.replace('.', '_').replace('-', '_').toUpperCase(Locale.ROOT);
    }

    private static void loadRequired(String fileName) {
        if (!load(fileName)) {
            throw new IllegalStateException(String.format("Файл %s не найден в ресурсах", fileName));
        }
    }

    private static void loadOptional(String fileName) {
        if (!load(fileName)) {
            log.info("Файл {} не найден, используются значения по умолчанию и переменные окружения", fileName);
        }
    }

    private static boolean load(String fileName) {
        try (InputStream stream = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (stream == null) {
                return false;
            }
            PROPERTIES.load(stream);
            return true;
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Ошибка при чтении %s", fileName), e);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
