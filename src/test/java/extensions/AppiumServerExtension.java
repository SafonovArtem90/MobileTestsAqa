package extensions;

import managers.AppiumServerManager;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * Запускает Appium Server один раз на весь прогон и останавливает после всех тестов.
 */
public class AppiumServerExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static final String STORE_KEY = AppiumServerExtension.class.getName();

    @Override
    public void beforeAll(ExtensionContext context) {
        context.getRoot().getStore(GLOBAL).getOrComputeIfAbsent(STORE_KEY, key -> {
            AppiumServerManager.startServer();
            return this;
        });
    }

    @Override
    public void close() {
        AppiumServerManager.stopServer();
    }
}
