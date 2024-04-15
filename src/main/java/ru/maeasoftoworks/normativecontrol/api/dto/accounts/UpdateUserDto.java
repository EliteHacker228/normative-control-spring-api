package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private Long academicGroupId;
    private Long normocontrollerId;
}
