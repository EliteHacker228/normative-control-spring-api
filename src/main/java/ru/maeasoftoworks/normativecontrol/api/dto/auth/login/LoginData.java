package ru.maeasoftoworks.normativecontrol.api.dto.auth.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.dto.auth.JsonSerializable;

@Getter
@Setter
public class LoginData extends JsonSerializable {
    private String email;
    private String password;
}
