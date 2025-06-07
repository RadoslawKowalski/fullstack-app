package com.example.pasir_kowalski_radoslaw.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test() {
        return "Hello, World!";
    }
    @GetMapping("/api/info")
    public String info() {
        return """
                {
                "appName": "Aplikacja Budżetowa",
                "version": "1.0",
                "message": "Witaj w aplikacji budżetowej stworzonej ze Spring
                Boot!"
                }""";
    }
}
