package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FieldsEnum {

    LOGIN("Логин"),
    PASSWORD("Пароль");

    private final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
