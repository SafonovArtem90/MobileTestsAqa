package android.login;

import base.BaseAndroidTest;
import config.TestConfig;
import enums.FieldsEnum;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import screens.login.LoginScenario;

import java.util.Locale;
import java.util.stream.Stream;

import static constants.Constants.ERROR_LOGIN_INVALID_SYMBOLS;
import static constants.Constants.ERROR_LOGIN_OR_PASSWORD_TEXT;
import static constants.Constants.ERROR_PASSWORD_MAX_LENGTH;
import static constants.Constants.ERROR_PASSWORD_MIN_LENGTH;
import static constants.Constants.REGEX_LOGIN;
import static constants.Constants.REGEX_PASSWORD;
import static constants.Constants.SUCCESS_LOGIN_TEXT;
import static utils.Assertions.assertMatchesRegex;
import static utils.Assertions.assertTextEquals;

@Epic("Мобильное приложение")
@Feature("Авторизация")
public class LoginTest extends BaseAndroidTest {

    private static final String LONG_VALUE_51_CHARS = "iIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIiIO";

    @Test
    @DisplayName("Успешный вход в приложение")
    public void checkSuccessLoginTest() {
        LoginScenario scenario = new LoginScenario();

        assertTextEquals(scenario.loginSuccessfully(TestConfig.getUserLogin(), TestConfig.getUserPassword()), SUCCESS_LOGIN_TEXT);
    }

    @ParameterizedTest(name = "Невалидный логин: «{0}»")
    @DisplayName("Валидация поля Логин")
    @MethodSource("invalidLogins")
    public void checkValidationLoginFieldTest(String login) {
        LoginScenario scenario = new LoginScenario();

        assertTextEquals(scenario.getLoginFieldError(login), ERROR_LOGIN_INVALID_SYMBOLS);
    }

    @ParameterizedTest(name = "Невалидный пароль длиной {0} символов, ошибка: «{2}»")
    @DisplayName("Валидация длины поля Пароль")
    @MethodSource("invalidPasswords")
    public void checkValidationPasswordFieldTest(int length, String password, String expectedError) {
        LoginScenario scenario = new LoginScenario();

        assertTextEquals(scenario.getPasswordFieldError(TestConfig.getUserLogin(), password), expectedError);
    }

    @ParameterizedTest(name = "Неверная пара логин/пароль: {0}")
    @DisplayName("Ошибка при входе с неверными учётными данными")
    @MethodSource("invalidCredentials")
    public void checkFailLoginTest(String caseName, String login, String password) {
        LoginScenario scenario = new LoginScenario();

        assertTextEquals(scenario.getErrorAfterSubmit(login, password), ERROR_LOGIN_OR_PASSWORD_TEXT);
    }

    @Test
    @DisplayName("Скрытие и отображение значения поля Пароль")
    public void checkViewHiddenAndVisiblePasswordTest() {
        LoginScenario scenario = new LoginScenario();

        scenario.togglePasswordVisibility(TestConfig.getUserPassword());
    }

    @ParameterizedTest(name = "Поле {0}: вставка значения «{1}»")
    @DisplayName("Удаление недопустимых символов после вставки из буфера обмена")
    @MethodSource("valuesWithInvalidCharacters")
    public void checkRemoveInvalidSymbolsAfterPasteTest(FieldsEnum field, String value, String regex) {
        LoginScenario scenario = new LoginScenario();

        assertMatchesRegex(scenario.pasteValue(field, value), regex);
    }

    static Stream<Arguments> valuesWithInvalidCharacters() {
        return Stream.of(
                Arguments.of(FieldsEnum.PASSWORD, LONG_VALUE_51_CHARS, REGEX_PASSWORD),
                Arguments.of(FieldsEnum.PASSWORD, "      " + LONG_VALUE_51_CHARS + "    ", REGEX_PASSWORD),
                Arguments.of(FieldsEnum.LOGIN, "user#123", REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN, LONG_VALUE_51_CHARS, REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN, "Артем", REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN, "     sdfsd sdfsd_,.sdfsd", REGEX_LOGIN),
                Arguments.of(FieldsEnum.LOGIN, "sdfsd .,/'_- sdf     ", REGEX_LOGIN)
        );
    }

    static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of(3, "tri", ERROR_PASSWORD_MIN_LENGTH),
                Arguments.of(51, LONG_VALUE_51_CHARS, ERROR_PASSWORD_MAX_LENGTH)
        );
    }

    static Stream<Arguments> invalidLogins() {
        return Stream.of(
                Arguments.of("tri"),
                Arguments.of("user#"),
                Arguments.of("USER123"),
                Arguments.of(LONG_VALUE_51_CHARS),
                Arguments.of("Артем"),
                Arguments.of("23456"),
                Arguments.of("")
        );
    }

    static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of("пароль в нижнем регистре", TestConfig.getUserLogin(), TestConfig.getUserPassword().toLowerCase(Locale.ROOT)),
                Arguments.of("логин в нижнем регистре", TestConfig.getUserLogin().toLowerCase(Locale.ROOT), TestConfig.getUserPassword()),
                Arguments.of("оба значения неверные", "Fail", "Failss")
        );
    }
}
