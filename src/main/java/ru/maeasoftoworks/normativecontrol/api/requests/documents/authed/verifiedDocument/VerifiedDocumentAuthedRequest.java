package ru.maeasoftoworks.normativecontrol.api.requests.documents.authed.verifiedDocument;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VerifiedDocumentAuthedRequest {
    @NotNull
    @NotBlank
    private final String documentId;
    @NotNull
    @NotBlank
    @Pattern(regexp = "\\b(?:html|docx)\\b|\\*")
    private final String documentType;
}
