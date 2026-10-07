package base;

import extensions.AppiumServerExtension;
import extensions.FailureAttachmentsExtension;
import managers.DriverManager;
import managers.app.AppManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Базовый тест: сессия Appium создаётся перед каждым тестом.
 * Закрытие сессии и вложения при падении — в FailureAttachmentsExtension
 * (он выполняется после @AfterEach и знает, упал тест или нет).
 */
@Tag("ANDROID")
@ExtendWith({AppiumServerExtension.class,
             FailureAttachmentsExtension.class})
public abstract class BaseAndroidTest {

    @BeforeEach
    public void startSession() {
        AppManager.resetApp(DriverManager.getDriver());
    }
}
