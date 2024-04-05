package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.University;

@Entity(name = "admins")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Admin extends User {

    @Builder
    public Admin(String email, String password, boolean isVerified, String firstName, String middleName,
                 String lastName, University university) {
        super.email = email;
        super.password = password;
        super.isVerified = isVerified;
        super.firstName = firstName;
        super.middleName = middleName;
        super.lastName = lastName;
        this.university = university;
    }

    @ManyToOne
    @JoinColumn(name = "university")
    private University university;

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
