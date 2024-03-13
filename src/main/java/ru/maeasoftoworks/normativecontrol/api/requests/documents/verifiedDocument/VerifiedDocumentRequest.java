package ru.maeasoftoworks.normativecontrol.api.requests.documents.verifiedDocument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VerifiedDocumentRequest {
    private final String documentId;
    private final String documentType;
}
