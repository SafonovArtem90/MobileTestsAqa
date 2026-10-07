package managers.app;

import config.TestConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * APK из ресурсов проекта (src/test/resources/{app.resource}).
 * Appium нужен абсолютный путь к файлу, поэтому ресурс из jar копируется во временный файл.
 */
public class ClasspathApkSource implements ApkSource {

    @Override
    public Optional<Path> findApk() {
        String resource = TestConfig.getAppResource();
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (url == null) {
            return Optional.empty();
        }
        try {
            if ("file".equals(url.getProtocol())) {
                return Optional.of(Path.of(url.toURI()).toAbsolutePath());
            }
            return Optional.of(copyToTempFile(url));
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("Некорректный путь к ресурсу %s", resource), e);
        }
    }

    @Override
    public String description() {
        return "ресурсы проекта (" + TestConfig.getAppResource() + ")";
    }

    private Path copyToTempFile(URL url) {
        try (InputStream stream = url.openStream()) {
            Path tempFile = Files.createTempFile("app-under-test", ".apk");
            tempFile.toFile().deleteOnExit();
            Files.copy(stream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось скопировать APK из ресурсов", e);
        }
    }
}
