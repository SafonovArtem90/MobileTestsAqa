package screens.login;

import base.BasePage;
import enums.AttributeEnum;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import static io.qameta.allure.model.Parameter.Mode.MASKED;

public class LoginPage extends BasePage {

    @AndroidFindBy(id = "tvTitle")
    private WebElement title;

    @AndroidFindBy(id = "etUsername")
    private WebElement loginInput;

    @AndroidFindBy(id = "etPassword")
    private WebElement passwordInput;

    @AndroidFindBy(accessibility = "Show password")
    private WebElement showPasswordIcon;

    @AndroidFindBy(id = "btnConfirm")
    private WebElement confirmButton;

    @AndroidFindBy(id = "tvError")
    private WebElement errorAfterSubmit;

    // Локатор временный: уточнить id ошибки под полем Логин в приложении.
    @AndroidFindBy(id = "anyIdFromInvalidValue")
    private WebElement loginFieldError;

    // Локатор временный: уточнить id ошибки под полем Пароль в приложении.
    @AndroidFindBy(id = "anyIdFromInvalidValue")
    private WebElement passwordFieldError;

    @Override
    @Step("Ожидание открытия экрана Login")
    protected void waitForPageLoaded() {
        elementActions.waitForVisible(title);
    }

    @Step("Ввод логина: {login}")
    public void enterLogin(String login) {
        elementActions.typeText(loginInput, login);
        deviceActions.hideKeyboard();
    }

    @Step("Ввод пароля")
    public void enterPassword(@Param(mode = MASKED) String password) {
        elementActions.typeText(passwordInput, password);
        deviceActions.hideKeyboard();
    }

    @Step("Вставка логина из буфера обмена")
    public void pasteLogin(String login) {
        deviceActions.setClipboardText(login);
        elementActions.pasteFromClipboard(loginInput);
    }

    @Step("Вставка пароля из буфера обмена")
    public void pastePassword(@Param(mode = MASKED) String password) {
        deviceActions.setClipboardText(password);
        elementActions.pasteFromClipboard(passwordInput);
    }

    @Step("Нажатие на кнопку Вход")
    public void clickLoginButton() {
        elementActions.click(confirmButton);
    }

    @Step("Нажатие на иконку «Показать пароль»")
    public void clickShowPasswordIcon() {
        elementActions.click(showPasswordIcon);
    }

    @Step("Ожидание отображения иконки «Показать пароль»")
    public void waitShowPasswordIconVisible() {
        elementActions.waitForVisible(showPasswordIcon);
    }

    @Step("Ожидание состояния иконки «Показать пароль»: включена = {isChecked}")
    public void waitShowPasswordIconChecked(boolean isChecked) {
        elementActions.waitForAttributeValue(showPasswordIcon, AttributeEnum.CHECKED, String.valueOf(isChecked));
    }

    @Step("Ожидание состояния поля Пароль: скрыт = {isHidden}")
    public void waitPasswordHidden(boolean isHidden) {
        elementActions.waitForAttributeValue(passwordInput, AttributeEnum.PASSWORD, String.valueOf(isHidden));
    }

    @Step("Получение заголовка экрана")
    public String getTitleText() {
        return elementActions.getText(title);
    }

    @Step("Получение текста ошибки после нажатия Вход")
    public String getErrorAfterSubmitText() {
        return elementActions.getText(errorAfterSubmit);
    }

    @Step("Получение текста ошибки поля Логин")
    public String getLoginFieldErrorText() {
        return elementActions.getText(loginFieldError);
    }

    @Step("Получение текста ошибки поля Пароль")
    public String getPasswordFieldErrorText() {
        return elementActions.getText(passwordFieldError);
    }

    @Step("Получение значения поля Логин")
    public String getLoginValue() {
        return elementActions.getText(loginInput);
    }

    @Step("Получение значения поля Пароль")
    public String getPasswordValue() {
        return elementActions.getText(passwordInput);
    }
}
