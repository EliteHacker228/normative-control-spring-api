package ru.maeasoftoworks.normativecontrol.api.domain.invites;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.maeasoftoworks.normativecontrol.api.domain.academical.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;

import java.util.Date;

@Entity(name = "invites")
@NoArgsConstructor
@Getter
@ToString
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Builder
    public Invite(Normocontroller owner, AcademicGroup academicGroup, Date createdAt, Date expiresAt) {
        this.owner = owner;
        this.academicGroup = academicGroup;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Normocontroller owner;

    @ManyToOne
    @JoinColumn(name = "academic_group")
    private AcademicGroup academicGroup;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "expires_at")
    private Date expiresAt;
}
