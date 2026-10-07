package screens.login;

import enums.FieldsEnum;
import io.qameta.allure.Step;

/**
 * Высокоуровневые шаги сценариев авторизации. Каждый метод — одна законченная
 * бизнес-операция: открытие экрана, ввод данных, отправка и возврат наблюдаемого результата.
 */
public class LoginScenario {

    @Step("Открыть экран входа и убедиться, что он открыт")
    public LoginPage openLoginPage() {
        LoginPage loginPage = new LoginPage();
        loginPage.waitShowPasswordIconVisible();
        loginPage.waitShowPasswordIconChecked(false);
        return loginPage;
    }

    @Step("Успешный вход")
    public String loginSuccessfully(String login, String password) {
        LoginPage loginPage = openLoginPage();
        loginPage.enterLogin(login);
        loginPage.enterPassword(password);
        loginPage.waitPasswordHidden(true);
        loginPage.clickLoginButton();
        return new SuccessLoginPage().getSuccessText();
    }

    @Step("Ввод невалидного логина и получение ошибки поля")
    public String getLoginFieldError(String login) {
        LoginPage loginPage = openLoginPage();
        loginPage.enterLogin(login);
        return loginPage.getLoginFieldErrorText();
    }

    @Step("Ввод невалидного пароля и получение ошибки поля")
    public String getPasswordFieldError(String login, String password) {
        LoginPage loginPage = openLoginPage();
        loginPage.enterLogin(login);
        loginPage.enterPassword(password);
        loginPage.waitPasswordHidden(true);
        return loginPage.getPasswordFieldErrorText();
    }

    @Step("Вход с неверными данными и получение текста ошибки")
    public String getErrorAfterSubmit(String login, String password) {
        LoginPage loginPage = openLoginPage();
        loginPage.enterLogin(login);
        loginPage.enterPassword(password);
        loginPage.waitPasswordHidden(true);
        loginPage.clickLoginButton();
        return loginPage.getErrorAfterSubmitText();
    }

    @Step("Переключение видимости пароля")
    public LoginPage togglePasswordVisibility(String password) {
        LoginPage loginPage = openLoginPage();
        loginPage.enterPassword(password);
        loginPage.waitPasswordHidden(true);
        loginPage.clickShowPasswordIcon();
        loginPage.waitPasswordHidden(false);
        loginPage.waitShowPasswordIconChecked(true);
        return loginPage;
    }

    @Step("Вставка значения через буфер обмена и получение фактического значения поля")
    public String pasteValue(FieldsEnum field, String value) {
        LoginPage loginPage = openLoginPage();
        return switch (field) {
            case LOGIN -> {
                loginPage.pasteLogin(value);
                yield loginPage.getLoginValue();
            }
            case PASSWORD -> {
                loginPage.pastePassword(value);
                yield loginPage.getPasswordValue();
            }
        };
    }
}
