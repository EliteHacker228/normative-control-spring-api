package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.validation.UniversityEmail;

@Getter
@Setter
public class UpdateUserEmailDto {
    @Email
    @UniversityEmail
    @NotNull
    @NotEmpty
    private String email;
}
