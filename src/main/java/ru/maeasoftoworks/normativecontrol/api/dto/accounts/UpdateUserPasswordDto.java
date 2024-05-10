package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UpdateUserPasswordDto {
    @NotNull
    @NotEmpty
    private String oldPassword;
    @NotNull
    @NotEmpty
    @Length(min = 8)
    private String newPassword;
}
