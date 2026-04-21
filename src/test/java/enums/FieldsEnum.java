package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FieldsEnum {

    LOGIN("login"),
    PASSWORD("password");

    private final String name;
}
