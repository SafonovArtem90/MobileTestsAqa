package managers.app;

import config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import managers.Device;
import managers.DeviceManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Готовит тестируемое приложение на устройстве:
 * 1. если приложение уже установлено — используем его;
 * 2. иначе ищем APK по пути из переменной окружения APP_PATH;
 * 3. иначе ищем APK в ресурсах проекта;
 * 4. перед тестом при необходимости очищаем данные и запускаем приложение.
 */
@Slf4j
public final class AppManager {

    private static final List<ApkSource> APK_SOURCES = List.of(new EnvironmentApkSource(), new ClasspathApkSource());

    private AppManager() {
    }

    @Step("Подготовка приложения на устройстве")
    public static void prepareApp(AndroidDriver driver) {
        String appPackage = TestConfig.getAppPackage();
        if (driver.isAppInstalled(appPackage)) {
            log.info("Приложение {} уже установлено", appPackage);
        } else {
            installApp(driver, appPackage);
        }

        if (TestConfig.isAppResetBeforeTest()) {
            // 1. Закрываем приложение, если оно открыто (например, осталось с прошлого запуска)
            driver.terminateApp(appPackage);
            // 2. Чистим данные и кэш через Appium
            driver.executeScript("mobile: clearApp", Map.of("appId", appPackage));
            // 3. Дублируем через adb pm clear — заодно сбрасываются выданные разрешения
            clearAppDataViaAdb(appPackage);
        }
        driver.activateApp(appPackage);
    }

    private static void clearAppDataViaAdb(String appPackage) {
        try {
            Device device = DeviceManager.getCurrentDevice();
            List<String> cmd = new java.util.ArrayList<>(List.of("adb", "shell", "pm", "clear", appPackage));
            if (device != null) {
                cmd.add(1, "-s");
                cmd.add(2, device.udid());
            }
            Process process = new ProcessBuilder(cmd).start();
            process.getInputStream().transferTo(System.out);
            process.waitFor();
        } catch (Exception e) {
            log.warn("Не удалось выполнить pm clear для {}", appPackage, e);
        }
    }

    private static void installApp(AndroidDriver driver, String appPackage) {
        for (ApkSource source : APK_SOURCES) {
            Path apk = source.findApk().orElse(null);
            if (apk != null) {
                log.info("Установка {} из источника «{}»: {}", appPackage, source.description(), apk);
                driver.installApp(apk.toString());
                return;
            }
        }
        throw new IllegalStateException(String.format(
                "Приложение %s не установлено, и APK не найден. Укажите APP_PATH или положите файл в src/test/resources/%s",
                appPackage, TestConfig.getAppResource()));
    }
}
