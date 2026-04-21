package extensions;

import managers.AppiumServerManager;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestExtension implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        AppiumServerManager.startServer();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        AppiumServerManager.stopServer();
    }
}
