package ru.maeasoftoworks.normativecontrol.api.dto.invites;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class InviteDto {
    private Long ownerId;
    private Long academicGroupId;
    private Date createdAt;
    private Date expiresAt;
}
