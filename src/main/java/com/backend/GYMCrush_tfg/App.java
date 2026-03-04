package com.backend.GYMCrush_tfg;

import org.springframework.boot.SpringApplication; // Importación necesaria
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class App {

   
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RequestMapping("/saludo")
    public String holaMundo() {
        return "¡Hola! Andrea!!!!!";
    }
}