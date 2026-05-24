package com.veterinaria.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ═══════════════════════════════════════════════════
// MS-CITAS · CitasApplication.java
// Microservicio central del dominio: agenda citas
// medicas para las mascotas.
//
// Es el ORQUESTADOR: se comunica con otros 2 MS:
//   - ms-usuarios (8081): valida que la mascota exista.
//   - ms-inventario (8083): descuenta stock del producto
//                           usado en la cita.
// La comunicacion se hace con WebClient.
// ═══════════════════════════════════════════════════

@SpringBootApplication
public class CitasApplication {
    public static void main(String[] args) {
        SpringApplication.run(CitasApplication.class, args);
    }
}
