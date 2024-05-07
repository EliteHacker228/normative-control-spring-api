package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserPasswordDto {
    @NotNull
    @NotEmpty
    private String oldPassword;
    @NotNull
    @NotEmpty
    private String newPassword;
}
