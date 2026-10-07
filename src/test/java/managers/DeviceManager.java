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
        List<String> configured = TestConfig.getDeviceUdids();
        if (configured.isEmpty()) {
            throw new IllegalStateException("Список устройств пуст: задайте devices.udids или DEVICES_UDIDS");
        }

        List<String> connected = getConnectedDevices();
        List<String> available = connected.stream().filter(configured::contains).toList();
        if (available.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Ни одно из настроенных устройств не подключено. Настроено: %s, подключено: %s", configured, connected));
        }
        if (available.size() < configured.size()) {
            log.warn("Пропущены устройства из конфига (не подключены): {}",
                     configured.stream().filter(d -> !connected.contains(d)).toList());
        }

        int port = TestConfig.getSystemPortStart();
        for (String udid : available) {
            DEVICES.add(new Device(udid, port++));
        }
        log.info("Пул устройств для запуска: {}", DEVICES);
    }

    /**
     * Список устройств, с которыми установлено соединение (статус device в adb).
     * Если adb недоступен — используется полный список из конфига (поведение по умолчанию).
     */
    private static List<String> getConnectedDevices() {
        try {
            Process process = new ProcessBuilder("adb", "devices").start();
            List<String> devices = process.getInputStream().readAllBytes()
                                          .toString()
                                          .lines()
                                          .skip(1)
                                          .map(line -> line.split("\\s+"))
                                          .filter(parts -> parts.length == 2 && parts[1].equals("device"))
                                          .map(parts -> parts[0])
                                          .toList();
            process.waitFor();
            log.info("Подключённые устройства (adb): {}", devices);
            return devices;
        } catch (Exception e) {
            log.warn("Не удалось получить список устройств через adb, используем список из конфига", e);
            return TestConfig.getDeviceUdids();
        }
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

    /**
     * Устройство, закреплённое за текущим потоком (может быть null, если не взято).
     */
    public static Device getCurrentDevice() {
        return CURRENT_DEVICE.get();
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
