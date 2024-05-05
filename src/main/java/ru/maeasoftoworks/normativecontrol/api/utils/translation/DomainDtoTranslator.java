package ru.maeasoftoworks.normativecontrol.api.utils.translation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Normocontroller;
import ru.maeasoftoworks.normativecontrol.api.dto.universities.NormocontrollerDto;
import ru.maeasoftoworks.normativecontrol.api.repositories.NormocontrollersRepository;

@Component
@RequiredArgsConstructor
public class DomainDtoTranslator {

    private final NormocontrollersRepository normocontrollersRepository;

    public NormocontrollerDto NormocontrollerToDto(Normocontroller normocontroller) {
        NormocontrollerDto normocontrollerDto = NormocontrollerDto.builder()
                .id(normocontroller.getId())
                .fullName(normocontroller.getFullName())
                .build();

        return normocontrollerDto;
    }

    public Normocontroller NormocontrollerFromDto(NormocontrollerDto normocontrollerDto) {
        return normocontrollersRepository.findNormocontrollerById(normocontrollerDto.getId());
    }
}
