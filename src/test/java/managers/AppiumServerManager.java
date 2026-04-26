package managers;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import lombok.extern.slf4j.Slf4j;
import config.ConfigLoader;

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
}
