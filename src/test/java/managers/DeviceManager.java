package managers;

import config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DeviceManager {

    private static final Queue<String> DEVICES = new ConcurrentLinkedQueue<>();
    private static final ThreadLocal<String> CURR_DEVICE_UDID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> CURR_SYSTEM_PORT = new ThreadLocal<>();
    private static final AtomicInteger PORT_NUMBER = new AtomicInteger(8200);

    static {
        String udids = ConfigLoader.getProperty("devices.udids");
        if (udids != null && !udids.isEmpty()) {
            Arrays.stream(udids.split(","))
                  .map(String::trim)
                  .forEach(DEVICES::add);
        }
        log.info("В config указаны девайсы для запуска: {}", DEVICES);
    }

    public static synchronized String getNextDevice() {
        if (CURR_DEVICE_UDID.get() != null) {
            return CURR_DEVICE_UDID.get();
        }

        String udid = DEVICES.poll();
        while (udid == null) {
            try {
                Thread.sleep(1000);
                udid = DEVICES.poll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ошибка в ожидании устройства");
            }
        }

        CURR_DEVICE_UDID.set(udid);
        CURR_SYSTEM_PORT.set(PORT_NUMBER.getAndIncrement());
        log.info("[THREAD-{}] Взял устройство из очереди: {}", Thread.currentThread().getId(), udid);
        return udid;
    }

    public static int getCurrentSystemPort() {
        return CURR_SYSTEM_PORT.get();
    }

    public static void returnDevice() {
        String udid = CURR_DEVICE_UDID.get();
        if (udid != null) {
            DEVICES.add(udid);
            log.info("[THREAD-{}] Вернул устройство в очередь: {}", Thread.currentThread().getId(), udid);
            CURR_DEVICE_UDID.remove();
            CURR_SYSTEM_PORT.remove();
        }
    }
}
