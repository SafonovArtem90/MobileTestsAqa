package utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import enums.AttributeEnum;
import org.openqa.selenium.WebElement;

public class ElementActions {
    private final WaitUtils waitUtils;
    private final AndroidDriver driver;

    public ElementActions(WaitUtils waitUtils, AndroidDriver driver) {
        this.waitUtils = waitUtils;
        this.driver = driver;

    }

    public void clickOnElement(WebElement element) {
        waitUtils.waitElementIsClickable(element);
        element.click();
    }

    public void typeTextIntoInputField(WebElement element, String text) {
        isElementIsVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    public void isElementIsVisible(WebElement element) {
        waitUtils.waitElementIsVisible(element);
    }

    public void isElementNotVisible(WebElement element) {
        waitUtils.waitElementNotVisible(element);
    }

    public String getTextOnElement(WebElement element) {
        isElementIsVisible(element);
        return element.getText();
    }

    public void checkAttributeValueOnElement(WebElement element, AttributeEnum attribute, String value) {
        waitUtils.waitElementAttributeToBe(element, attribute.getName(), value);
    }

    public void pasteTextFromClipboard(WebElement element) {
        clickOnElement(element);
        driver.pressKey(new KeyEvent(AndroidKey.PASTE));
    }
}
