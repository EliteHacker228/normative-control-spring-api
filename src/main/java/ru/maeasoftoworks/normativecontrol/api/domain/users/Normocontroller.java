package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.*;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.University;

@Entity(name = "normocontrollers")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Normocontroller extends User {

    @Builder
    public Normocontroller(String email, String password, boolean isVerified, String firstName,
                           String middleName, String lastName, University university, int documentsLimit) {
        super.email = email;
        super.password = password;
        super.isVerified = isVerified;
        super.firstName = firstName;
        super.middleName = middleName;
        super.lastName = lastName;
        super.university = university;
        this.documentsLimit = documentsLimit;
    }

    @Column(name = "documents_limit")
    @Setter
    private int documentsLimit;

    private final Role role = Role.NORMOCONTROLLER;

    @Override
    public Role getRole() {
        return this.role;
    }
}
