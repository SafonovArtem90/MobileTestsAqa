package utils;

import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Param;
import io.qameta.allure.Step;

import static io.qameta.allure.model.Parameter.Mode.MASKED;

public class DeviceActions {

    private final AndroidDriver driver;

    public DeviceActions(AndroidDriver driver) {
        this.driver = driver;
    }

    @Step("Скрыть клавиатуру")
    public void hideKeyboard() {
        if (driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
    }

    @Step("Скопировать текст в буфер обмена устройства")
    public void setClipboardText(@Param(mode = MASKED) String text) {
        driver.setClipboardText(text);
    }
}
