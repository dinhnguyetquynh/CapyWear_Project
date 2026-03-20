package org.example.clothing_be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TestController {
    @GetMapping("/hello")
    public String getHelloWord(){
        String str = "Hello DNQ";
        return str;
    }
}
