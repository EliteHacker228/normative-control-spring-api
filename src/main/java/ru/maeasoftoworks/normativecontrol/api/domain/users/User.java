package ru.maeasoftoworks.normativecontrol.api.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.University;

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
    protected String email;

    @JsonIgnore
    @Column(name = "password")
    protected String password;

    @Column(name = "is_verified")
    protected boolean isVerified;

    @Column(name = "first_name")
    protected String firstName;

    @Column(name = "middle_name")
    protected String middleName;

    @Column(name = "last_name")
    protected String lastName;

    @ManyToOne
    @JoinColumn(name = "university")
    protected University university;

    public abstract Role getRole();
}
