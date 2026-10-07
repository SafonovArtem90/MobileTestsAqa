package managers;

/**
 * Устройство из пула с закреплённым за ним systemPort для UiAutomator2.
 */
public record Device(String udid, int systemPort) {
}
