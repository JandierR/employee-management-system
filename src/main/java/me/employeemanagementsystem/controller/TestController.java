package me.employeemanagementsystem.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class TestController {
    @GetMapping("/test-connection")
    public Map<String, String> testConnection() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Connection Successful!");
        return response;
    }
}
