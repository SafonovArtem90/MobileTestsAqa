package managers;

import config.TestConfig;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Пул устройств для параллельного запуска. Поток берёт устройство на время теста и возвращает после.
 * За каждым устройством закреплён постоянный systemPort, чтобы на устройстве не копились процессы UiAutomator2.
 */
@Slf4j
public final class DeviceManager {

    private static final BlockingQueue<Device> DEVICES = new LinkedBlockingQueue<>();
    private static final ThreadLocal<Device> CURRENT_DEVICE = new ThreadLocal<>();

    static {
        List<String> udids = TestConfig.getDeviceUdids();
        if (udids.isEmpty()) {
            throw new IllegalStateException("Список устройств пуст: задайте devices.udids или DEVICES_UDIDS");
        }
        int port = TestConfig.getSystemPortStart();
        for (String udid : udids) {
            DEVICES.add(new Device(udid, port++));
        }
        log.info("Пул устройств для запуска: {}", DEVICES);
    }

    private DeviceManager() {
    }

    public static Device acquireDevice() {
        Device current = CURRENT_DEVICE.get();
        if (current != null) {
            return current;
        }

        Duration timeout = TestConfig.getDeviceWaitTimeout();
        try {
            Device device = DEVICES.poll(timeout.toSeconds(), TimeUnit.SECONDS);
            if (device == null) {
                throw new IllegalStateException(String.format("Нет свободного устройства в течение %s", timeout));
            }
            CURRENT_DEVICE.set(device);
            log.info("Взято устройство из пула: {}", device.udid());
            return device;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Ожидание свободного устройства прервано", e);
        }
    }

    public static void releaseDevice() {
        Device device = CURRENT_DEVICE.get();
        if (device != null) {
            CURRENT_DEVICE.remove();
            DEVICES.add(device);
            log.info("Устройство возвращено в пул: {}", device.udid());
        }
    }
}
