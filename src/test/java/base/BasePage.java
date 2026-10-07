package base;

import config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import managers.DriverManager;
import org.openqa.selenium.support.PageFactory;
import utils.DeviceActions;
import utils.ElementActions;
import utils.WaitUtils;

import java.time.Duration;

/**
 * Базовый экран. При создании инициализирует элементы и ждёт загрузку экрана.
 */
public abstract class BasePage {

    protected final ElementActions elementActions;
    protected final DeviceActions deviceActions;

    protected BasePage() {
        AndroidDriver driver = DriverManager.getDriver();
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ZERO), this);

        WaitUtils waitUtils = new WaitUtils(driver, TestConfig.getExplicitWait());
        this.elementActions = new ElementActions(waitUtils, driver);
        this.deviceActions = new DeviceActions(driver);
        waitForPageLoaded();
    }

    /**
     * Проверка, что экран открыт: ожидание ключевого элемента.
     */
    protected abstract void waitForPageLoaded();
}
