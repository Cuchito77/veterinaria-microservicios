package com.veterinaria.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ═══════════════════════════════════════════════════
// API Gateway (Spring Cloud Gateway).
// Punto unico de entrada (puerto 8080). Enruta las
// peticiones hacia los microservicios usando sus nombres
// logicos en Eureka (lb://ms-xxx). Las rutas se definen
// en application.yml.
// ═══════════════════════════════════════════════════

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
