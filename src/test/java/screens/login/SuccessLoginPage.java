package screens.login;

import base.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

public class SuccessLoginPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Вход в Alfa-Test выполнен']")
    private WebElement successLabel;

    @Override
    @Step("Ожидание открытия экрана успешного входа")
    protected void waitForPageLoaded() {
        elementActions.waitForVisible(successLabel);
    }

    @Step("Получение текста об успешном входе")
    public String getSuccessText() {
        return elementActions.getText(successLabel);
    }
}
