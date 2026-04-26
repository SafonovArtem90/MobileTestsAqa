package extensions;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static managers.AppiumServerManager.startServer;
import static managers.AppiumServerManager.stopServer;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class TestExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static volatile boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (started) {
            return;
        }

        synchronized (TestExtension.class) {
            if (!started) {
                startServer();
                started = true;
                context.getRoot().getStore(GLOBAL).put("suite", this);
            }
        }
    }

    @Override
    public void close() {
        stopServer();
    }
}
