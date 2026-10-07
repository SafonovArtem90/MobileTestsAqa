package managers.app;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Источник APK-файла (паттерн Strategy).
 */
public interface ApkSource {

    Optional<Path> findApk();

    String description();
}
