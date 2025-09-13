package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrchestrationController {

    @GetMapping("/hello")
    public String test() {
        return "hi";
    }
}
