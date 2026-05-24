package com.veterinaria.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ═══════════════════════════════════════════════════
// MS-LOGIN · LoginApplication.java
// Microservicio de autenticacion SIMPLE: valida
// usuario + contrasena contra la tabla "usuarios".
// (Sin JWT, segun lo definido para este proyecto.)
// ═══════════════════════════════════════════════════

@SpringBootApplication
public class LoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }
}
