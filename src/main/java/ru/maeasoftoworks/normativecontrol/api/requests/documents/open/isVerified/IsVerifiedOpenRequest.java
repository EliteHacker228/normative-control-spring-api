package ru.maeasoftoworks.normativecontrol.api.requests.documents.open.isVerified;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class IsVerifiedOpenRequest {

    @NotNull(message = "Document ID can not be null")
    @NotEmpty(message = "Document ID can not be empty")
    @NotBlank(message = "Document ID can not be blank")
    private final String documentId;
}
