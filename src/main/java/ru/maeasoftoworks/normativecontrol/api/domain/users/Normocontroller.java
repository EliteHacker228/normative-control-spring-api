package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "normocontrollers")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Normocontroller extends User {

    @Builder
    public Normocontroller(String email, String password, boolean isVerified, String firstName,
                           String middleName, String lastName, int documentsLimit) {
        super.email = email;
        super.password = password;
        super.isVerified = isVerified;
        super.firstName = firstName;
        super.middleName = middleName;
        super.lastName = lastName;
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
