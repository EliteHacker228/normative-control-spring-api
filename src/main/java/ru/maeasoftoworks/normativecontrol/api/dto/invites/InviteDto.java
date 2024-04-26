package ru.maeasoftoworks.normativecontrol.api.dto.invites;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class InviteDto {
    private Long academicGroupId;
    private Date createdAt;
    private Date expiresAt;
}
