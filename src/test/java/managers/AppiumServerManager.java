package managers;

import config.TestConfig;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Slf4j
public final class AppiumServerManager {

    private static AppiumDriverLocalService service;

    private AppiumServerManager() {
    }

    public static synchronized void startServer() {
        if (!TestConfig.isAppiumAutoRun()) {
            log.info("Автозапуск Appium отключён, используется сервер {}", getServerUrl());
            return;
        }
        if (service != null && service.isRunning()) {
            return;
        }

        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withIPAddress(TestConfig.getAppiumIp());
        builder.usingPort(TestConfig.getAppiumPort());
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, TestConfig.getAppiumLogLevel());

        service = AppiumDriverLocalService.buildService(builder);
        service.start();
        log.info("Appium Server запущен: {}", service.getUrl());
    }

    public static synchronized void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            log.info("Appium Server остановлен");
        }
    }

    public static URL getServerUrl() {
        try {
            return URI.create(String.format("http://%s:%d", TestConfig.getAppiumIp(), TestConfig.getAppiumPort())).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Некорректный адрес Appium Server", e);
        }
    }
}
