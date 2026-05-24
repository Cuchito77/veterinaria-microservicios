package com.veterinaria.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ═══════════════════════════════════════════════════
// MS-USUARIOS · UsuariosApplication.java
// Microservicio que gestiona los duenos (clientes de
// la veterinaria) y sus mascotas.
// Al arrancar, Flyway ejecuta los scripts V1, V2, V3
// en orden y crea las tablas en la BD vet_usuarios_db.
// ═══════════════════════════════════════════════════

@SpringBootApplication
public class UsuariosApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsuariosApplication.class, args);
    }
}
