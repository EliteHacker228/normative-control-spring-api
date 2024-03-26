package ru.maeasoftoworks.normativecontrol.api.requests.account.account.get;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;

public class AccountResponse {
      public AccountResponse(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.middleName = user.getMiddleName();
        this.lastName = user.getLastName();
        this.academicGroup = user.getAcademicGroup();
        this.organization = user.getOrganization();
        this.isVerified = user.getIsVerified();
        this.role = user.getRole();
    }

    private String email;

    private String firstName;

    private String middleName;

    private String lastName;

    private String academicGroup;

    private String organization;

    private Boolean isVerified;

    private Role role;
}
