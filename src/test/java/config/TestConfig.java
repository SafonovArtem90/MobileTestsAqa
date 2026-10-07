package config;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Типизированный доступ к настройкам. Все ключи собраны в одном месте.
 */
public final class TestConfig {

    private TestConfig() {
    }

    public static boolean isAppiumAutoRun() {
        return ConfigLoader.getBoolean("appium.server.autoRun");
    }

    public static String getAppiumIp() {
        return ConfigLoader.getProperty("appium.server.ip");
    }

    public static int getAppiumPort() {
        return ConfigLoader.getInt("appium.server.port");
    }

    public static String getAppiumLogLevel() {
        return ConfigLoader.getProperty("appium.server.logLevel");
    }

    public static List<String> getDeviceUdids() {
        return Arrays.stream(ConfigLoader.getProperty("devices.udids").split(","))
                     .map(String::trim)
                     .filter(udid -> !udid.isEmpty())
                     .toList();
    }

    public static int getSystemPortStart() {
        return ConfigLoader.getInt("devices.systemPort.start");
    }

    public static Duration getDeviceWaitTimeout() {
        return Duration.ofSeconds(ConfigLoader.getInt("devices.wait.timeout"));
    }

    public static String getPlatformName() {
        return ConfigLoader.getProperty("platform.name");
    }

    public static String getAppPackage() {
        return ConfigLoader.getProperty("app.package");
    }

    /**
     * Путь к APK только из -Dapp.path или переменной окружения APP_PATH (config-файлы не учитываются).
     */
    public static Optional<String> getAppPathFromEnvironment() {
        return ConfigLoader.getFromEnvironment("app.path");
    }

    public static String getAppResource() {
        return ConfigLoader.getProperty("app.resource");
    }

    public static boolean isAppResetBeforeTest() {
        return ConfigLoader.getBoolean("app.resetBeforeTest");
    }

    public static Duration getExplicitWait() {
        return Duration.ofSeconds(ConfigLoader.getInt("explicit.wait"));
    }

    public static Duration getNewCommandTimeout() {
        return Duration.ofSeconds(ConfigLoader.getInt("appium.newCommandTimeout"));
    }

    public static String getUserLogin() {
        return ConfigLoader.getProperty("test.user.login");
    }

    public static String getUserPassword() {
        return ConfigLoader.getProperty("test.user.password");
    }
}
