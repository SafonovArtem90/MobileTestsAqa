package utils;

import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Step;

public class DeviceActions {
    private final AndroidDriver driver;

    public DeviceActions(AndroidDriver driver) {
        this.driver = driver;
    }

    @Step("Спрятать клавиатуру")
    public void hideKeyboardOnDevice() {
        driver.hideKeyboard();
    }

    @Step("Скопировать текст в буфер обмена устройства '{0}'")
    public void setClipboardText(String text) {
        driver.setClipboardText(text);
    }
}
