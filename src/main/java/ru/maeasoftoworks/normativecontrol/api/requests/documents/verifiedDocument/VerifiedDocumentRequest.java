package ru.maeasoftoworks.normativecontrol.api.requests.documents.verifiedDocument;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class VerifiedDocumentRequest {
    @NotNull
    @NotBlank
    private final String documentId;
    @NotNull
    @NotBlank
    @Size(min = 36, max = 36)
    @Pattern(regexp = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
    private final String fingerprint;
    @NotNull
    @NotBlank
    @Pattern(regexp = "\\b(?:html|docx)\\b|\\*")
    private final String documentType;
}
