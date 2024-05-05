package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "admins")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Admin extends User {

    @Builder
    public Admin(String email, String password, boolean isVerified, String fullName) {
        super.email = email;
        super.password = password;
        super.isVerified = isVerified;
        super.fullName = fullName;
    }

    private Role role = Role.ADMIN;

    @Override
    public Role getRole() {
        return this.role;
    }
}
