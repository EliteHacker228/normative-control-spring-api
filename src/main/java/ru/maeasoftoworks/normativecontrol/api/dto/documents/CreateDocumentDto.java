package ru.maeasoftoworks.normativecontrol.api.dto.documents;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;
import ru.maeasoftoworks.normativecontrol.api.validation.cyrillicName.CyrillicFullName;
import ru.maeasoftoworks.normativecontrol.api.validation.docxdocument.DocxDocument;
import ru.maeasoftoworks.normativecontrol.api.validation.lengthinbytes.LengthInBytes;

@Getter
@Setter
public class CreateDocumentDto {
    @NotNull(message = "document is missing")
    @DocxDocument(message = "document format must be .docx")
    @LengthInBytes(min = 1, max = 20971520, message = "document can not be smaller than 1 Byte or bigger than 20 MBytes")
    private MultipartFile document;
}
