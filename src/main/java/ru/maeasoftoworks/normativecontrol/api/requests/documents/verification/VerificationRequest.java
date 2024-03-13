package ru.maeasoftoworks.normativecontrol.api.requests.documents.verification;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
@Setter
public class VerificationRequest {
    @NotNull(message = "Document can not be null")
    private MultipartFile document;
}
