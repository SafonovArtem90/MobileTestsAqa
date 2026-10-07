package base;

import extensions.AppiumServerExtension;
import extensions.FailureAttachmentsExtension;
import managers.DriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Базовый тест: сессия Appium создаётся перед каждым тестом и закрывается после.
 * Конкретные экраны создаются в тестовых классах.
 */
@Tag("ANDROID")
@ExtendWith({AppiumServerExtension.class,
             FailureAttachmentsExtension.class})
public abstract class BaseAndroidTest {

    @BeforeEach
    public void startSession() {
        DriverManager.getDriver();
    }

    @AfterEach
    public void closeSession() {
        DriverManager.quitDriver();
    }
}
