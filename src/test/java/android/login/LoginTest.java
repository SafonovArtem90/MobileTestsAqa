package android.login;

import base.BaseAndroidTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import constants.Constants;
import enums.FieldsEnum;
import screens.login.SuccessLoginPage;
import utils.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static constants.Constants.ERROR_PASSWORD_MAX_LENGTH;
import static constants.Constants.ERROR_PASSWORD_MIN_LENGTH;
import static constants.Constants.REGEX_LOGIN;
import static constants.Constants.REGEX_PASSWORD;
import static utils.Assertions.assertTextEqual;
import static utils.RegexUtils.isMatch;

@Epic("Мобильное приложение")
@Feature("Авторизация")
public class LoginTest extends BaseAndroidTest {

    @Test
    @DisplayName("Успешный логин в приложение.")
    @Tags(@Tag("ANDROID"))
    public void checkSuccessLoginTest() {
        loginPage
                .checkShowPasswordIconIsVisible()
                .checkStatusShowPasswordIcon(false)
                .enterLogin(Constants.VALID_LOGIN)
                .enterPassword(Constants.VALID_PASSWORD)
                .checkIsHiddenPasswordInput(true)
                .clickLoginButton();
        new SuccessLoginPage();
    }

    @ParameterizedTest(name = "Ввод в поле Логин не валидного значения: {0}.")
    @DisplayName("Проверка валидации поля Логин.")
    @Tags(@Tag("ANDROID"))
    @MethodSource("valuesOfLoginField")
    public void checkValidationLoginFieldTest(String login) {
        loginPage
                .checkShowPasswordIconIsVisible()
                .checkStatusShowPasswordIcon(false)
                .enterLogin(login);

        assertTextEqual(Constants.ERROR_LOGIN_ENTER_TEXT,
                        loginPage.getErrorLoginFieldText());
    }

    @ParameterizedTest(name = "Ввод в поле Пароль не валидного значения: {0} с ож.ошибкой: {1}.")
    @DisplayName("Проверка валидации поля Пароль.")
    @Tags(@Tag("ANDROID"))
    @MethodSource("valuesOfPasswordField")
    public void checkValidationPasswordFieldTest(String password, String errorText) {
        loginPage
                .checkShowPasswordIconIsVisible()
                .checkStatusShowPasswordIcon(false)
                .enterLogin(Constants.VALID_LOGIN)
                .enterPassword(password)
                .checkIsHiddenPasswordInput(true);

        assertTextEqual(errorText,
                        loginPage.getErrorPasswordFieldText());
    }

    @ParameterizedTest(name = "Ввод Логин: {0} и Пароль: {1}.")
    @DisplayName("Проверка отображения ошибки при нажатии Войти с неверным вводом Логин или Пароль.")
    @Tags(@Tag("ANDROID"))
    @MethodSource("failValuesOfAuthorize")
    public void checkFailLoginTest(String login, String password) {
        loginPage
                .checkShowPasswordIconIsVisible()
                .checkStatusShowPasswordIcon(false)
                .enterLogin(login)
                .enterPassword(password)
                .checkIsHiddenPasswordInput(true)
                .clickLoginButton();
        loginPage.checkErrorTextIsVisible();

        assertTextEqual(Constants.ERROR_LOGIN_OR_PASSWORD_ENTER_TEXT,
                        loginPage.getErrorAfterSubmitErrorText());
    }

    @Test
    @DisplayName("Проверка отображения '*' и видимого значения для поля Пароль.")
    @Tags(@Tag("ANDROID"))
    public void checkViewHiddenAndVisiblePasswordTest() {
        loginPage
                .checkShowPasswordIconIsVisible()
                .checkStatusShowPasswordIcon(false)
                .enterPassword(Constants.VALID_PASSWORD)
                .checkIsHiddenPasswordInput(true)
                .clickShowPasswordIconButton()
                .checkIsHiddenPasswordInput(false)
                .checkStatusShowPasswordIcon(true);
    }

    @ParameterizedTest(name = "Ввод в поле {0} значения {1}")
    @DisplayName("Проверка обрезания недопустимых символов после вставки из буфера.")
    @Tags(@Tag("ANDROID"))
    @MethodSource("listValuesForRemoveInvalidCharacter")
    public void checkRemoveInvalidSymbolsWithPastValueTest(String field, String value, String regex) {
        String expectValue = null;
        loginPage
                .checkShowPasswordIconIsVisible()
                .checkStatusShowPasswordIcon(false);

        if (field.equals(FieldsEnum.PASSWORD.getName())) {
            expectValue = loginPage
                    .enterPasswordViaClipboard(value)
                    .getInputFieldTextFromPassword();
        } else if (field.equals(FieldsEnum.LOGIN.getName())) {
            expectValue = loginPage
                    .enterLoginViaClipboard(value)
                    .getInputFieldTextFromLogin();
        }

        Assertions.assertTrue(isMatch(expectValue, regex), String.format("Исправленное значение '%s' не соответствует REGEX('%s') поля.",
                                                                         expectValue, regex));
    }

    static Stream<Arguments> listValuesForRemoveInvalidCharacter() {
        return Stream.of(
                Arguments.of(FieldsEnum.PASSWORD.getName(), "iIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIO", REGEX_PASSWORD),
                Arguments.of(FieldsEnum.PASSWORD.getName(), "      iIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiI    ", REGEX_PASSWORD),
                Arguments.of(FieldsEnum.LOGIN.getName(), "user#123", REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN.getName(), "iIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIO", REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN.getName(), "Артем", REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN.getName(), "     sdfsd sdfsd_,.sdfsd", REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN.getName(), "sdfsd .,/'_- sdf     ", REGEX_LOGIN)
        );
    }

    static Stream<Arguments> valuesOfPasswordField() {
        return Stream.of(
                Arguments.of("tri", ERROR_PASSWORD_MIN_LENGTH),
                Arguments.of("iIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIO", ERROR_PASSWORD_MAX_LENGTH)
        );
    }

    static Stream<Arguments> valuesOfLoginField() {
        return Stream.of(
                Arguments.of("tri"),
                Arguments.of("user#123"),
                Arguments.of("iIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIO"),
                Arguments.of("Артем"),
                Arguments.of("23456"),
                Arguments.of("")
        );
    }

    static Stream<Arguments> failValuesOfAuthorize() {
        return Stream.of(
                Arguments.of("Login", "password"),
                Arguments.of("login", "Password"),
                Arguments.of("Fail", "Failss")
        );
    }
}
