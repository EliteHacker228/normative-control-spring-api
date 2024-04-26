package ru.maeasoftoworks.normativecontrol.api.domain.academical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "academic_groups")
@NoArgsConstructor
@ToString
@Getter
public class AcademicGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    public AcademicGroup(String name) {
        this.name = name;
    }

    @Column(name = "name", unique = true)
    @Setter
    private String name;
}
