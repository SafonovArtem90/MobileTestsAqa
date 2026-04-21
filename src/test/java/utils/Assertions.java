package utils;

import io.qameta.allure.Step;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {

    @Step("Проверка равенства строк {0} и {1}")
    public static void assertTextEqual(String actual, String expected) {
        assertThat(actual).as("Проверка текста").isEqualTo(expected);
    }

    @Step("Проверка условия {0} для {1}")
    public static void assertTrue(boolean condition, String message) {
        assertThat(condition).as(message).isTrue();
    }
}
