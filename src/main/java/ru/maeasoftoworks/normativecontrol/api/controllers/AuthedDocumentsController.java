package ru.maeasoftoworks.normativecontrol.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents/authed")
public class AuthedDocumentsController {

    // TODO: Controller: list, выдаёт данные о работе, которую проверял юзер

    @GetMapping("/list")
    public ResponseEntity<String> getLisOfVerificationsForUser() {
        return null;
    }

    // TODO: Controller: find, выдаёт список работ по запрошенным параметрам
    // email: adfadf@urfu.me
    // group: RI-400004
    // name: Кузнецов М. А.
    // afterDate: DD.MM.YYYY:HH:MM:SS (нижняя граница дипазона дат)
    // beforeDate: DD.MM.YYYY:HH:MM:SS (верхняя граница дипазона дат)
}
