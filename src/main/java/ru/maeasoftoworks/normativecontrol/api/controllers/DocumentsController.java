package ru.maeasoftoworks.normativecontrol.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Document;
import ru.maeasoftoworks.normativecontrol.api.domain.documents.Result;
import ru.maeasoftoworks.normativecontrol.api.domain.users.*;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.CreateDocumentDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentReportDto;
import ru.maeasoftoworks.normativecontrol.api.dto.documents.DocumentVerdictDto;
import ru.maeasoftoworks.normativecontrol.api.services.DocumentsService;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Отвечает за вход, загрузку, получение, и удаление документов, а также за доклад о ошибках и вынесение вердикта")
public class DocumentsController {
    private final DocumentsService documentsService;
    private final JwtService jwtService;

    @Operation(
            summary = "Получение списка документов, загруженных в сервис,  автоматическая проверка которых была завершена",
            description = """
                    Позволяет получить документы, загруженные в сервис, автоматическую проверку которых была успешна
                    завершена (проверяющиеся в данный момент и провалившие автоматическую проверку вызвав ошибку,
                    которую модуль проверки документов не смог обработать, документы - не возвращаются).
                    Для администратора - вернёт список всех документов, загруженных в сервис. Для
                    нормоконтролёра - список всех документов, загруженных
                    в сервис студентами из групп, к которым он приписан. Для студента - вернёт список всех документов,
                    которые он отправлял на проверку.""",
            responses = {
                    @ApiResponse(description = "Документы получены успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
//                    @ApiResponse(description = "Не верно указаны данные для доступа к ресурсу", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping
    public List<Document> getDocuments(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken) {
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.getDocuments(user);
    }

    @Operation(
            summary = "Получение администратором списка всех документов, загруженных в сервис",
            description = """
                    Позволяет получить все документы, загруженные в сервис, вне зависимости от того, прошли
                    они автоматическую проверку или нет.""",
            responses = {
                    @ApiResponse(description = "Документы получены успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
//                    @ApiResponse(description = "Не верно указаны данные для доступа к ресурсу", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/all")
    public List<Document> getAllDocuments(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken) {
        return documentsService.getAllDocuments();
    }

    @Operation(
            summary = "Получение нормоконтролёром последнего автоматически проверенного документа от каждого студента из всех приписанных к нормоконтролёру групп",
            description = """
                    Позволяет получить последний документ, прошдший проверку, от студента из каждой группы, приписанной к нормоконтролёру.""",
            responses = {
                    @ApiResponse(description = "Документы получены успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
//                    @ApiResponse(description = "Не верно указаны данные для доступа к ресурсу", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/actual")
    public List<Document> getActualDocuments(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken) {
        Normocontroller normocontroller = (Normocontroller) jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.getActualDocuments(normocontroller);
    }

    @Operation(
            summary = "Получение нормоконтролёром выгрузки о студентах и их работах",
            description = """
                    Позволяет получить выгрузку в формате .csv, содержащую информацию о студентах, количестве
                    их попыток прохождения нормоконтроля, результате, дате последней проверке, и т.д.""",
            responses = {
                    @ApiResponse(description = "Выгрузка получена успешно", responseCode = "200", content = @Content(mediaType = "text/plain")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
//                    @ApiResponse(description = "Не верно указаны данные для доступа к ресурсу", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/csv")
    public String getDocumentsCsv(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken) {
        User user = jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.getDocumentsCsv(user);
    }

    @Operation(
            summary = "Загрузка студентом работы на проверку",
            description = """
                    Позволяет студенту загрузить работу на проверку.""",
            responses = {
                    @ApiResponse(description = "Документ загружен успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Вы не имеет доступ к данному методу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(implementation = CreateDocumentDto.class)
            )
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping
    public Document createDocument(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                   @ModelAttribute @Valid CreateDocumentDto createDocumentDto) {
        Student student = (Student) jwtService.getUserFromAuthorizationHeader(bearerToken);
        return documentsService.createDocument(student, createDocumentDto);
    }

    @Operation(
            summary = "Удаление администратором документа из сервиса",
            description = """
                    Позволяет администратору удалить документ из сервиса по его ID.
                    """,
            responses = {
                    @ApiResponse(description = "Документ удалён успешно", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Документ с указанным ID не найден", responseCode = "404", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
            @Content(
                    mediaType = "multipart/form-data",
                    schema = @Schema(implementation = CreateDocumentDto.class)
            )
    })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{document_id}")
    public ResponseEntity<JSONObject> deleteDocument(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                                     @PathVariable("document_id") @Parameter(description = "Идентификатор документа") Long documentId) {
        Admin admin = (Admin) jwtService.getUserFromAuthorizationHeader(bearerToken);
        documentsService.deleteDocument(admin, documentId);
        JSONObject response = new JSONObject();
        response.put("message", "Document with id " + documentId + " deleted successfully");
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Получение работы",
            description = """
                    Позволяет получить статус работу по её ID в одном из трёх форматов, указанном в documentType:
                    docx (работа, в которой через примечания Word выделены все ошибки),
                    html (html документ, выстроенный на основе документа).
                    Для администратора - вернёт список Result для всех документов, загруженных в сервис. Для
                    нормоконтролёра - список Result для всех документов, загруженных
                    в сервис студентами из групп, к которым он приписан. Для студента - вернёт список Result для всех документов,
                    которые он отправлял на проверку.""",
            responses = {
                    @ApiResponse(description = "Документ получен успешно", responseCode = "200", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(description = "Документа с указанным ID не найден", responseCode = "404", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{document_id}")
    @SneakyThrows
    public ResponseEntity getDocumentByType(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                            @PathVariable("document_id") @Parameter(description = "Идентификатор документа") Long documentId,
                                            @RequestParam(name = "type") @Parameter(description = "Тип документа: docx, html, node, source") String documentType) {

        if (documentType.equals("node"))
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(documentsService.getDocumentNode(documentId));

        byte[] documentBytes;
        documentBytes = documentsService.getResult(documentId, documentType);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(documentBytes);
    }

    @Operation(
            summary = "Получение статуса работы",
            description = """
                    Позволяет получить статус проверки работы по её ID в составе объекта Result.
                    Для администратора - вернёт список Result для всех документов, загруженных в сервис. Для
                    нормоконтролёра - список Result для всех документов, загруженных
                    в сервис студентами из групп, к которым он приписан. Для студента - вернёт список Result для всех документов,
                    которые он отправлял на проверку.""",
            responses = {
                    @ApiResponse(description = "Статус по документу в составе Result получен успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))),
                    @ApiResponse(description = "Статус для документа с указанным ID не найден", responseCode = "404", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/{document_id}/status")
    public Result getDocumentVerificationStatus(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                                @PathVariable("document_id") @Parameter(description = "Идентификатор документа") Long documentId) {
        return documentsService.getDocumentVerificationStatus(documentId);
    }

    @Operation(summary = "Пометка ошибки в работе студентом как сомнительной",
            description = """
                   Позволяет студенту доложить о сомнительной ошибке в документе по его ID. В случае, если об ошибке
                   уже доложение - ничего не произойдёт. При успешном выполнении запроса возвращается запись о самом документе.""",
            responses = {
                    @ApiResponse(description = "Ошибка в документе отмечена успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Документ с указанным ID не найден", responseCode = "404", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{document_id}/report")
    public Document reportOnDocument(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                     @PathVariable("document_id") @Parameter(description = "Идентификатор документа") Long documentId,
                                     @RequestBody @Valid DocumentReportDto documentReportDto) {
        return documentsService.reportOnDocument(documentId, documentReportDto);
    }

    @Operation(summary = "Отмена пометки ошибки в работе студентом как сомнительной",
            description = """
                   Позволяет студенту отменить пометку о сомнительной ошибке в документе по его ID. При успешном выполнении запроса возвращается запись о самом документе.""",
            responses = {
                    @ApiResponse(description = "Ошибка в документе отмечена успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Документ с указанным ID не найден", responseCode = "404", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{document_id}/report")
    public Document unreportOnDocument(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                       @PathVariable("document_id") @Parameter(description = "Идентификатор документа") Long documentId,
                                       @RequestBody @Valid DocumentReportDto documentReportDto) {
        return documentsService.unreportOnDocument(documentId, documentReportDto);
    }

    @Operation(summary = "Вынесение вердикта о работе нормоконтролёром",
            description = """
                   Позволяет  нормоконтролёру принять или отклонить работу, спороводив её комментарием (комментарий опционален)""",
            responses = {
                    @ApiResponse(description = "Вердикт сохранён успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Документ с указанным ID не найден", responseCode = "404", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{document_id}/verdict")
    public Document makeVerdict(@Parameter(hidden = true) @RequestHeader("Authorization") String bearerToken,
                                @PathVariable("document_id") @Parameter(description = "Идентификатор документа") Long documentId,
                                @RequestBody @Valid DocumentVerdictDto documentVerdictDto) {
        return documentsService.makeVerdictOnDocument(documentId, documentVerdictDto);
    }


    @Operation(summary = "Получение списка работ студента самим студентом, нормоконтролером и администратором",
            description = """
                   Позволяет студенту, нормоконтролеру и администратору получить список работ, загруженных студентом.
                   Студент может получить только свои работы, нормоконтролер - работы любого студента из всех приписанных ему групп,
                   администратор - работы любого студенты""",
            responses = {
                    @ApiResponse(description = "Список работ получен успешно", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class))),
                    @ApiResponse(description = "Студент с указанным ID не найден", responseCode = "404", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "Вы не имеет доступ к данному ресурсу", responseCode = "403", content = @Content(mediaType = "application/json")),
                    @ApiResponse(description = "В запросе указаны некорректные данные", responseCode = "400", content = @Content(mediaType = "application/json")),
            })
    @SecurityRequirement(name = "JWT")
    @GetMapping("/students/{student_id}")
    public List<Document> getDocumentsByStudentId(@PathVariable("student_id") @Parameter(description = "Идентификатор студента") Long studentId){
        return documentsService.getDocumentsByStudentId(studentId);
    }
}
