package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    private String fullName;
    private Long academicGroupId;
}
