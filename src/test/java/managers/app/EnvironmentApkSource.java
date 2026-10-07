package managers.app;

import config.TestConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * APK по пути из -Dapp.path или переменной окружения APP_PATH.
 */
public class EnvironmentApkSource implements ApkSource {

    @Override
    public Optional<Path> findApk() {
        return TestConfig.getAppPathFromEnvironment()
                         .map(path -> {
                             Path apk = Path.of(path).toAbsolutePath();
                             if (!Files.isRegularFile(apk)) {
                                 throw new IllegalStateException(String.format("APP_PATH указывает на несуществующий файл: %s", apk));
                             }
                             return apk;
                         });
    }

    @Override
    public String description() {
        return "переменная окружения APP_PATH";
    }
}
