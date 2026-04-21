package managers;

import lombok.extern.slf4j.Slf4j;
import config.ConfigLoader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;

@Slf4j
public class DriverManager {
    private static AndroidDriver driver;

    public static AndroidDriver getDriver() {
        if (driver == null) {
            createDriver();
        }
        return driver;
    }

    private static void createDriver() {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
        options.setPlatformName(ConfigLoader.getProperty("platform.name"));
        options.setDeviceName(ConfigLoader.getProperty("device.name"));

        String apkInstall = ConfigLoader.getProperty("app.install");
        if (apkInstall.equals(Boolean.TRUE.toString())) {
            options.setApp(ConfigLoader.getProperty("app.path"));
            log.info("Установка и запуск приложения.");
        } else {
            options.setAppPackage(ConfigLoader.getProperty("app.package"));
            options.setNoReset(false);
            log.info("Приложение не будет установлено. Запуск установленного.");
        }
        driver = new AndroidDriver(AppiumServerManager.getServiceUrl(), options);
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
