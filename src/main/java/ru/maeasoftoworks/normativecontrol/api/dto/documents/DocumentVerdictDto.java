package ru.maeasoftoworks.normativecontrol.api.dto.documents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.DocumentVerdict;

@Getter
@Setter
@Schema(description = "Сущность выставления вердикта документу")
public class DocumentVerdictDto {
    @Schema(description = "Сам вердикт (0 - работа не проверена, 1 - работа принята, 2 - работа отклонена)")
    @NotNull(message = "verdict is missing")
    private DocumentVerdict verdict;
    // TODO: Увеличить лимит на длину комментария
    @Length(max = 255, message = "comment can not be longer than 255")
    private String comment;
}
