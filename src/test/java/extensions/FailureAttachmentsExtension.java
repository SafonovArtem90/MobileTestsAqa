package extensions;

import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import managers.DriverManager;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;

/**
 * При падении теста прикладывает к отчёту Allure скриншот и дерево элементов экрана.
 */
@Slf4j
public class FailureAttachmentsExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty()) {
            return;
        }
        DriverManager.getDriverIfCreated().ifPresent(this::attachArtifacts);
    }

    private void attachArtifacts(AndroidDriver driver) {
        try {
            Allure.addAttachment("Скриншот при падении", "image/png",
                                 new ByteArrayInputStream(driver.getScreenshotAs(OutputType.BYTES)), ".png");
            Allure.addAttachment("Дерево элементов экрана", "text/xml", driver.getPageSource(), ".xml");
        } catch (RuntimeException e) {
            log.warn("Не удалось приложить материалы к отчёту", e);
        }
    }
}
