package ru.maeasoftoworks.normativecontrol.api.domain.users;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "normocontrollers")
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
public class Normocontroller extends User {

    @Builder
    public Normocontroller(String email, String password, boolean isVerified, String fullName) {
        super.email = email;
        super.password = password;
        super.isVerified = isVerified;
        super.fullName = fullName;
    }

    private final Role role = Role.NORMOCONTROLLER;

    @Override
    public Role getRole() {
        return this.role;
    }
}
