package ru.maeasoftoworks.normativecontrol.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public AcademicGroup(University university, String name) {
        this.university = university;
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "university")
    private University university;

    @Column(name = "name")
    private String name;
}
