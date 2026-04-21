package screens.login;

import base.BasePage;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import enums.AttributeEnum;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    @AndroidFindBy(id = "tvTitle")
    private WebElement loginTvTitle;

    @AndroidFindBy(id = "etUsername")
    private WebElement loginInput;

    @AndroidFindBy(id = "etPassword")
    private WebElement passwordInput;

    @AndroidFindBy(accessibility = "Show password")
    private WebElement showPasswordIcon;

    @AndroidFindBy(id = "btnConfirm")
    private WebElement confirmButton;

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"com.alfabank.qapp:id/tvError\").text(\"Введены неверные данные\")")
    private WebElement errorTextAfterLogin;

    @AndroidFindBy(id = "anyIdFromInvalidValue")
    private WebElement errorTextToLoginField;

    @AndroidFindBy(id = "anyIdFromInvalidValue")
    private WebElement errorTextToPasswordField;

    @Override
    @Step("Проверка отображения заголовка окна Login")
    protected void checkPageLoaded() {
        elementActions.isElementIsVisible(loginTvTitle);
    }

    @Step("Ввод логина: {login}")
    public LoginPage enterLogin(String login) {
        elementActions.typeTextIntoInputField(loginInput, login);
        deviceActions.hideKeyboardOnDevice();
        return this;
    }

    @Step("Ввод пароля: {password}")
    public LoginPage enterPassword(String password) {
        elementActions.typeTextIntoInputField(passwordInput, password);
        deviceActions.hideKeyboardOnDevice();
        return this;
    }

    @Step("Нажатие на кнопку Вход")
    public void clickLoginButton() {
        elementActions.clickOnElement(confirmButton);
    }

    @Step("Получение текста заголовка окна Login")
    public String getTvTittleText() {
        return elementActions.getTextOnElement(loginTvTitle);
    }

    @Step("Проверка отображения ошибки входа")
    public LoginPage checkErrorTextIsVisible() {
        elementActions.isElementIsVisible(errorTextAfterLogin);
        return this;
    }

    @Step("Получение текста ошибки после нажатия Вход")
    public String getErrorAfterSubmitErrorText() {
        return elementActions.getTextOnElement(errorTextAfterLogin);
    }

    @Step("Проверка отображения иконки 'показывать пароль'")
    public LoginPage checkShowPasswordIconIsVisible() {
        elementActions.isElementIsVisible(showPasswordIcon);
        return this;
    }

    @Step("Нажать на иконку 'показывать пароль'")
    public LoginPage clickShowPasswordIconButton() {
        elementActions.clickOnElement(showPasswordIcon);
        return this;
    }

    @Step("Проверка состояния иконки 'показывать пароль': {isChecked}")
    public LoginPage checkStatusShowPasswordIcon(boolean isChecked) {
        elementActions.checkAttributeValueOnElement(showPasswordIcon, AttributeEnum.CHECKED, String.valueOf(isChecked));
        return this;
    }

    @Step("Получение текста ошибки при вводе невалидного Логин")
    public String getErrorLoginFieldText() {
        return elementActions.getTextOnElement(errorTextToLoginField);
    }

    @Step("Получение текста ошибки при вводе невалидного Пароль")
    public String getErrorPasswordFieldText() {
        return elementActions.getTextOnElement(errorTextToPasswordField);
    }

    @Step("Получение текста из поля Пароль")
    public String getInputFieldTextFromPassword() {
        return elementActions.getTextOnElement(passwordInput);
    }

    @Step("Получение текста из поля Логин")
    public String getInputFieldTextFromLogin() {
        return elementActions.getTextOnElement(loginInput);
    }

    @Step("Проверка отображения сокрытия пароля '*'")
    public LoginPage checkIsHiddenPasswordInput(boolean isChecked) {
        elementActions.checkAttributeValueOnElement(passwordInput, AttributeEnum.PASSWORD, String.valueOf(isChecked));
        return this;
    }

    @Step("Вставляет текст из буфера обмена в поле Логин")
    public LoginPage enterLoginViaClipboard(String login) {
        deviceActions.setClipboardText(login);
        elementActions.pasteTextFromClipboard(loginInput);
        return this;
    }

    @Step("Вставляет текст из буфера обмена в поле Пароль")
    public LoginPage enterPasswordViaClipboard(String login) {
        deviceActions.setClipboardText(login);
        elementActions.pasteTextFromClipboard(passwordInput);
        return this;
    }
}
