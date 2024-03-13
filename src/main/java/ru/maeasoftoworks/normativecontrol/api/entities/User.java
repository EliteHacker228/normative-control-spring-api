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

    public User(String email, String name, String login, String plainTextPassword, Role role, String organization) {
        this.email = email;
        this.name = name;
        this.login = login;
        this.password = HashingUtils.sha256(plainTextPassword);
        this.role = role;
        this.isVerified = false;
        this.organization = organization;
    }

    @Column(name = "email")
    @Getter
    @Setter
    private String email;

    @Column(name = "name")
    @Getter
    @Setter
    private String name;

    @Column(name = "login")
    @Getter
    @Setter
    private String login;

    @Column(name = "password")
    @Getter
    private String password;
    public void setPassword(String plainTextPassword) {
        this.password = HashingUtils.sha256(plainTextPassword);
    }

    @Column(name = "roles")
    @Getter
    @Setter
    private Role role;

    @Column(name = "isVerified")
    @Getter
    @Setter
    private Boolean isVerified;

    @Column(name = "organization")
    @Getter
    @Setter
    private String organization;
}