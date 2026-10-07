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
        try {
            if (context.getExecutionException().isPresent()) {
                DriverManager.getDriverIfCreated().ifPresent(this::attachArtifacts);
            }
        } finally {
            // Закрываем сессию после обработки результата теста
            DriverManager.quitDriver();
        }
    }

    private void attachArtifacts(AndroidDriver driver) {
        try {
            String pageSource = driver.getPageSource();
            // Дублируем дерево экрана в System.out, чтобы при отладке CI не нужен был отчёт
            System.out.println("PAGE SOURCE при падении:\n" + pageSource);
            Allure.addAttachment("Скриншот при падении", "image/png",
                                 new ByteArrayInputStream(driver.getScreenshotAs(OutputType.BYTES)), ".png");
            Allure.addAttachment("Дерево элементов экрана", "text/xml", pageSource, ".xml");
        } catch (RuntimeException e) {
            log.warn("Не удалось приложить материалы к отчёту", e);
        }
    }
}
