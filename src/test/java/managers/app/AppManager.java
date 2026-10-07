package managers.app;

import config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import managers.Device;

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
    public static void prepareApp(AndroidDriver driver, Device device) {
        String appPackage = TestConfig.getAppPackage();
        dismissSysUiAnr(driver);
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
            clearAppDataViaAdb(appPackage, device.udid());
        }
        driver.activateApp(appPackage);
    }

    private static void clearAppDataViaAdb(String appPackage, String udid) {
        try {
            List<String> cmd = new java.util.ArrayList<>();
            cmd.add("adb");
            if (udid != null && !udid.isBlank()) {
                cmd.add("-s");
                cmd.add(udid);
            }
            cmd.add("shell");
            cmd.add("pm");
            cmd.add("clear");
            cmd.add(appPackage);
            Process process = new ProcessBuilder(cmd).start();
            process.getInputStream().transferTo(System.out);
            process.waitFor();
        } catch (Exception e) {
            log.warn("Не удалось выполнить pm clear для {}", appPackage, e);
        }
    }

    public static void resetApp(AndroidDriver driver) {
        String appPackage = TestConfig.getAppPackage();
        dismissSysUiAnr(driver);
        if (TestConfig.isAppResetBeforeTest()) {
            driver.terminateApp(appPackage);
            driver.executeScript("mobile: clearApp", Map.of("appId", appPackage));
            clearAppDataViaAdb(appPackage, getUdid(driver));
        }
        driver.activateApp(appPackage);
    }

    private static String getUdid(AndroidDriver driver) {
        try {
            return (String) driver.getCapabilities().getCapability("udid");
        } catch (Exception e) {
            log.warn("Не удалось определить udid драйвера", e);
            return "";
        }
    }

    private static void dismissSysUiAnr(AndroidDriver driver) {
        try {
            String source = driver.getPageSource();
            if (source.contains("System UI isn't responding") || source.contains("Системный интерфейс не отвечает")) {
                driver.findElement(org.openqa.selenium.By.id("android:id/aerr_wait")).click();
                log.warn("Дисмисован диалог System UI isn't responding кнопкой Wait");
            }
        } catch (Exception e) {
            log.warn("Не удалось обработать диалог System UI", e);
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
