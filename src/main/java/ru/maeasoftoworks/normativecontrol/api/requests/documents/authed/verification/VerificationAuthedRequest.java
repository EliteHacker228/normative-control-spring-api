package ru.maeasoftoworks.normativecontrol.api.requests.documents.authed.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class VerificationAuthedRequest {
    @NotNull(message = "Document can not be null")
    private final MultipartFile document;
}
