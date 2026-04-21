package constants;

public class Constants {
    // данные для теста
    public static final String VALID_LOGIN = "Login";
    public static final String VALID_PASSWORD = "Password";
    public static final String REGEX_LOGIN = "^[a-zA-Z .,/'_-]{4,50}$";
    public static final String REGEX_PASSWORD = "^.{4,50}$";

    // сообщения об ошибках
    public static final String ERROR_LOGIN_OR_PASSWORD_ENTER_TEXT = "Введены неверные данные";
    public static final String ERROR_LOGIN_ENTER_TEXT = "Введены недопустимые символы в Логин";
    public static final String ERROR_PASSWORD_MIN_LENGTH = "Длина меньше допустимой";
    public static final String ERROR_PASSWORD_MAX_LENGTH = "Длина превышает допустимую";
}
