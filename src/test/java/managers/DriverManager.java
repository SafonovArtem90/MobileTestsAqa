package managers;

import config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import lombok.extern.slf4j.Slf4j;
import managers.app.AppManager;

import java.util.Optional;

@Slf4j
public final class DriverManager {

    private static final ThreadLocal<AndroidDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager() {
    }

    public static AndroidDriver getDriver() {
        if (DRIVER_THREAD_LOCAL.get() == null) {
            createDriver();
        }
        return DRIVER_THREAD_LOCAL.get();
    }

    /**
     * Возвращает драйвер, только если он уже создан. Нужно для расширений (скриншоты),
     * чтобы случайно не запустить новую сессию.
     */
    public static Optional<AndroidDriver> getDriverIfCreated() {
        return Optional.ofNullable(DRIVER_THREAD_LOCAL.get());
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER_THREAD_LOCAL.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (RuntimeException e) {
            log.warn("Ошибка при закрытии сессии Appium", e);
        } finally {
            DRIVER_THREAD_LOCAL.remove();
            DeviceManager.releaseDevice();
        }
    }

    private static void createDriver() {
        Device device = DeviceManager.acquireDevice();
        UiAutomator2Options options = buildOptions(device);
        try {
            AndroidDriver driver = new AndroidDriver(AppiumServerManager.getServerUrl(), options);
            DRIVER_THREAD_LOCAL.set(driver);
            log.info("Сессия Appium создана на устройстве {}", device.udid());
            AppManager.prepareApp(driver, device);
        } catch (RuntimeException e) {
            quitDriver();
            throw new IllegalStateException(String.format("Не удалось подготовить сессию на устройстве %s", device.udid()), e);
        }
    }

    private static UiAutomator2Options buildOptions(Device device) {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName(TestConfig.getPlatformName());
        options.setUdid(device.udid());
        options.setSystemPort(device.systemPort());
        options.setNewCommandTimeout(TestConfig.getNewCommandTimeout());
        options.setAutoGrantPermissions(true);
        options.setNoReset(true);
        return options;
    }
}
