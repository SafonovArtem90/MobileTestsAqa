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
     * Опрашивает adb несколько раз с паузой, потому что при старте эмулятора
     * устройство может некоторое время отображаться как offline.
     * Если adb недоступен или ничего не вернул — откатывается к списку из конфига.
     */
    private static List<String> getConnectedDevices() {
        for (int attempt = 1; attempt <= 6; attempt++) {
            List<String> devices = queryAdb();
            log.info("Подключённые устройства (adb), попытка {}: {}", attempt, devices);
            if (!devices.isEmpty()) {
                return devices;
            }
            if (attempt < 6) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        log.warn("adb devices не вернул устройств за 30 секунд, используем список из конфига: {}", TestConfig.getDeviceUdids());
        return TestConfig.getDeviceUdids();
    }

    private static List<String> queryAdb() {
        try {
            Process process = new ProcessBuilder("adb", "devices").start();
            String output = new String(process.getInputStream().readAllBytes());
            log.info("RAW adb devices:{}", output.replace("\r", "\\r").replace("\n", "\\n"));
            List<String> devices = output.lines().skip(1)
                                         .map(line -> line.split("\\s+"))
                                         .filter(parts -> parts.length == 2 && parts[1].equals("device"))
                                         .map(parts -> parts[0])
                                         .toList();
            process.waitFor();
            return devices;
        } catch (Exception e) {
            log.warn("Не удалось опросить adb", e);
            return List.of();
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

    public static void releaseDevice() {
        Device device = CURRENT_DEVICE.get();
        if (device != null) {
            CURRENT_DEVICE.remove();
            DEVICES.add(device);
            log.info("Устройство возвращено в пул: {}", device.udid());
        }
    }
}
