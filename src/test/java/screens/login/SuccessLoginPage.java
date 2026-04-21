package screens.login;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import base.BasePage;
import org.openqa.selenium.WebElement;

public class SuccessLoginPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='Вход в Alfa-Test выполнен']")
    private WebElement successLoginLabel;

    @Override
    @Step("Проверка отображения заголовка окна SuccessLogin")
    protected void checkPageLoaded() {
        elementActions.isElementIsVisible(successLoginLabel);
    }
}
