package managers;

import lombok.extern.slf4j.Slf4j;
import config.ConfigLoader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.remote.AutomationName;

import java.net.URL;

@Slf4j
public class DriverManager {

    private static final ThreadLocal<AndroidDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    public static AndroidDriver getDriver() {
        if (DRIVER_THREAD_LOCAL.get() == null) {
            createDriver();
        }
        return DRIVER_THREAD_LOCAL.get();
    }

    private static void createDriver() {
        String udid = DeviceManager.getNextDevice();
        int systemPort = DeviceManager.getCurrentSystemPort();

        UiAutomator2Options options = new UiAutomator2Options();
        options.setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2);
        options.setPlatformName(ConfigLoader.getProperty("platform.name"));
        options.setUdid(udid);
        options.setSystemPort(systemPort);

        String apkInstall = ConfigLoader.getProperty("app.install");
        if (apkInstall.equals(Boolean.TRUE.toString())) {
            options.setApp(ConfigLoader.getProperty("app.path"));
            log.info("Установка и запуск приложения для {}", udid);
        } else {
            options.setAppPackage(ConfigLoader.getProperty("app.package"));
            options.setNoReset(false);
            log.info("Приложение не будет установлено. Запуск установленного на {}", udid);
        }
        try {
            AndroidDriver driver = new AndroidDriver(getServiceUrl(), options);
            DRIVER_THREAD_LOCAL.set(driver);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Не смогло создать driver с такими options: %s", options), e);
        }
    }

    public static void quitDriver() {
        if (DRIVER_THREAD_LOCAL.get() != null) {
            DRIVER_THREAD_LOCAL.get().quit();
            DRIVER_THREAD_LOCAL.remove();
            DeviceManager.returnDevice();
        }
    }

    private static URL getServiceUrl() {
        try {
            return new URL("http://" + ConfigLoader.getProperty("appium.server.ip") + ":"
                                   + ConfigLoader.getProperty("appium.server.port"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
