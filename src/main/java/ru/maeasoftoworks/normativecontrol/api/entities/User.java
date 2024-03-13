package ru.maeasoftoworks.normativecontrol.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.utils.HashingUtils;

import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@ToString
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    private Long id;

    public User(String email, String name, String login, String plainTextPassword, Role role, String organization) {
        this.email = email;
        this.name = name;
        this.login = login;
        this.password = plainTextPassword;
        this.role = role.toString();
        this.isVerified = false;
        this.organization = organization;
    }

    @Column(name = "email", unique = true)
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
        this.password = plainTextPassword;
    }

    @Column(name = "role")
    @Getter
    @Setter
    private String role;

    @Column(name = "isVerified")
    @Getter
    @Setter
    private Boolean isVerified;

    @Column(name = "organization")
    @Getter
    @Setter
    private String organization;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}