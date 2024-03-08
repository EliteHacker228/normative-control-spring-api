package ru.maeasoftoworks.normativecontrol.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommonController {
    @GetMapping("/hello")
    @ResponseBody
    private String sayHello(){
        return "Hewwwo :3";
    }
}
