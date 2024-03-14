package ru.maeasoftoworks.normativecontrol.api.requests.documents.open.verification;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class VerificationOpenRequest {
    @NotNull(message = "Document can not be null")
    private final MultipartFile document;

    @NotNull
    @NotBlank
    @Size(min = 36, max = 36)
    @Pattern(regexp = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
    private final String fingerprint;
}
