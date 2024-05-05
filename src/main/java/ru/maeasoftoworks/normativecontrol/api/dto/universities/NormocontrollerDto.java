package ru.maeasoftoworks.normativecontrol.api.dto.universities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NormocontrollerDto {
    private Long id;
    private String fullName;
}
