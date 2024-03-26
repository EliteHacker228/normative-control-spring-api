package ru.maeasoftoworks.normativecontrol.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;

import java.util.List;

@Entity(name = "users")
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    private Long id;

    public User(String email, String password, String firstName, String middleName, String lastName, String academicGroup, String organization, Role role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.academicGroup = academicGroup;
        this.organization = organization;
        this.role = role;
    }

    @Column(name = "email")
    @Getter
    @Setter
    private String email;

    @Column(name = "password")
    @Getter
    private String password;

    public void setPassword(String plainTextPassword) {
        this.password = HashingUtils.sha256(plainTextPassword);
    }

    @Column(name = "first_name")
    @Getter
    @Setter
    private String firstName;

    @Column(name = "middle_name")
    @Getter
    @Setter
    private String middleName;

    @Column(name = "last_name")
    @Getter
    @Setter
    private String lastName;

    @Column(name = "academic_group")
    @Getter
    @Setter
    private String academicGroup;

    @Column(name = "organization")
    @Getter
    @Setter
    private String organization;

    @Column(name = "is_verified")
    @Getter
    @Setter
    private Boolean isVerified = false;

    @Column(name = "role")
    @Getter
    @Setter
    private Role role;
}