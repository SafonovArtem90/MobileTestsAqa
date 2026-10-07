package utils;

import io.qameta.allure.Step;

import static org.assertj.core.api.Assertions.assertThat;

public final class Assertions {

    private Assertions() {
    }

    @Step("Проверка: фактический текст «{actual}» равен ожидаемому «{expected}»")
    public static void assertTextEquals(String actual, String expected) {
        assertThat(actual).as("Проверка текста").isEqualTo(expected);
    }

    @Step("Проверка: «{actual}» соответствует шаблону «{regex}»")
    public static void assertMatchesRegex(String actual, String regex) {
        assertThat(actual)
                .as("Значение '%s' не соответствует шаблону '%s'", actual, regex)
                .isNotNull()
                .matches(regex);
    }
}
