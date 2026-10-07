package constants;

/**
 * Ожидаемые значения из требований. Учётные данные здесь не хранятся: см. TestConfig.
 */
public final class Constants {

    public static final String REGEX_LOGIN = "^[a-zA-Z .,/'_-]{4,50}$";
    public static final String REGEX_PASSWORD = "^.{4,50}$";

    public static final String SUCCESS_LOGIN_TEXT = "Вход в Alfa-Test выполнен";

    public static final String ERROR_LOGIN_OR_PASSWORD_TEXT = "Введены неверные данные";
    public static final String ERROR_LOGIN_INVALID_SYMBOLS = "Введены недопустимые символы в Логин";
    public static final String ERROR_PASSWORD_MIN_LENGTH = "Длина меньше допустимой";
    public static final String ERROR_PASSWORD_MAX_LENGTH = "Длина превышает допустимую";

    private Constants() {
    }
}
