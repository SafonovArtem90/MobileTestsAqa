package utils;

import enums.AttributeEnum;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.openqa.selenium.WebElement;

public class ElementActions {

    private final WaitUtils waitUtils;
    private final AndroidDriver driver;

    public ElementActions(WaitUtils waitUtils, AndroidDriver driver) {
        this.waitUtils = waitUtils;
        this.driver = driver;
    }

    public void click(WebElement element) {
        waitUtils.waitElementIsClickable(element);
        element.click();
    }

    public void typeText(WebElement element, String text) {
        waitForVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    public void waitForVisible(WebElement element) {
        waitUtils.waitElementIsVisible(element);
    }

    public void waitForInvisible(WebElement element) {
        waitUtils.waitElementNotVisible(element);
    }

    public String getText(WebElement element) {
        waitForVisible(element);
        return element.getText();
    }

    public void waitForAttributeValue(WebElement element, AttributeEnum attribute, String value) {
        waitUtils.waitElementAttributeToBe(element, attribute.getName(), value);
    }

    public void pasteFromClipboard(WebElement element) {
        click(element);
        driver.pressKey(new KeyEvent(AndroidKey.PASTE));
    }
}
