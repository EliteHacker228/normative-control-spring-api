package ru.maeasoftoworks.normativecontrol.api.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@ToString
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    protected long id;

    @Column(name = "email", unique = true)
    @Setter
    protected String email;

    @JsonIgnore
    @Column(name = "password")
    @Setter
    protected String password;

    @Column(name = "is_verified")
    @Setter
    protected boolean isVerified;

    @Column(name = "first_name")
    @Setter
    protected String firstName;

    @Column(name = "middle_name")
    @Setter
    protected String middleName;

    @Column(name = "last_name")
    @Setter
    protected String lastName;

    public abstract Role getRole();
}
