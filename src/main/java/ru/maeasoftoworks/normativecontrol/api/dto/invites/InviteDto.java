package ru.maeasoftoworks.normativecontrol.api.dto.invites;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import ru.maeasoftoworks.normativecontrol.api.domain.universities.AcademicGroup;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;

import java.util.Date;

@Getter
@Setter
public class InviteDto {
    private Long academicGroupId;
    private Date createdAt;
    private Date expiresAt;
}
