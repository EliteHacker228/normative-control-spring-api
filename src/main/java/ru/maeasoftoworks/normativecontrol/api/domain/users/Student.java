package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;

@Entity(name = "students")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Student extends User {

    @Builder
    public Student(String email, String password, boolean isVerified,
                   String fullName, AcademicGroup academicGroup,
                   int documentsLimit) {
        super();
        super.email = email;
        super.password = password;
        super.isVerified = isVerified;
        super.fullName = fullName;
        this.academicGroup = academicGroup;
        this.documentsLimit = documentsLimit;
    }

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "academic_group_id")
    @Setter
    private AcademicGroup academicGroup;

    @Column(name = "documents_limit")
    @Setter
    private int documentsLimit;

    private final Role role = Role.STUDENT;

    @Override
    public Role getRole() {
        return this.role;
    }
}
