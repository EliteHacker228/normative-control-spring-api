package ru.maeasoftoworks.normativecontrol.api.dto.accounts;

import ru.maeasoftoworks.normativecontrol.api.domain.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.University;

public class UserDto {
    protected long id;
    protected String email;
    protected boolean isVerified;
    protected String firstName;
    protected String middleName;
    protected String lastName;
}
