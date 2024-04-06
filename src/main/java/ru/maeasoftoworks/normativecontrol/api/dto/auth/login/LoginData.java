package ru.maeasoftoworks.normativecontrol.api.dto.auth.login;

import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.dto.JsonSerializable;

@Getter
@Setter
public class LoginData extends JsonSerializable {
    private String email;
    private String password;
}
