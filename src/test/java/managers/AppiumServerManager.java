package managers;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import lombok.extern.slf4j.Slf4j;
import config.ConfigLoader;

import java.net.URL;

@Slf4j
public class AppiumServerManager {

    private static AppiumDriverLocalService service;

    public static void startServer() {
        if (Boolean.parseBoolean(ConfigLoader.getProperty("appium.server.autoRun"))) {
            AppiumServiceBuilder builder = new AppiumServiceBuilder();
            builder.withIPAddress(ConfigLoader.getProperty("appium.server.ip"))
                   .usingPort(Integer.parseInt(ConfigLoader.getProperty("appium.server.port")))
                   .withArgument(GeneralServerFlag.LOG_LEVEL, "error");

            service = AppiumDriverLocalService.buildService(builder);
            service.start();
            log.info("Appium Server запущен");
        }
    }

    public static void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            log.info("Appium Server остановлен");
        }
    }

    public static URL getServiceUrl() {
        if (service != null) {
            return service.getUrl();
        }
        try {
            return new URL("http://" + ConfigLoader.getProperty("appium.server.ip") + ":" + ConfigLoader.getProperty("appium.server.port"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
