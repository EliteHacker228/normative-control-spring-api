package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.University;

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
        this.university = university;
        this.documentsLimit = documentsLimit;
    }

    @ManyToOne
    @JoinColumn(name = "university")
    private University university;

    @Column(name = "documents_limit")
    private int documentsLimit;

    @Override
    public Role getRole() {
        return Role.NORMOCONTROLLER;
    }
}
