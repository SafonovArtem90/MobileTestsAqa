package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AttributeEnum {

    CHECKED("checked"),
    PASSWORD("password");

    private final String name;
}
