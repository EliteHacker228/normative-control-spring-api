package ru.maeasoftoworks.normativecontrol.api.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "universities")
@NoArgsConstructor
@ToString
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    public University(String name) {
        this.name = name;
    }

    @Column(name = "name")
    private String name;
}
