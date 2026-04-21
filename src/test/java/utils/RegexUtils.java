package utils;

import java.util.regex.Pattern;

public final class RegexUtils {

    public static boolean isMatch(String text, String regex) {
        return Pattern.matches(regex, text);
    }
}
