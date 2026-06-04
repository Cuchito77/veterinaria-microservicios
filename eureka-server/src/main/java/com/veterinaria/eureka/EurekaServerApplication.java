package com.veterinaria.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// ═══════════════════════════════════════════════════
// Servidor Eureka (Service Registry).
// @EnableEurekaServer lo convierte en el registro central
// donde los microservicios se inscriben y se descubren.
// Consola web: http://localhost:8761
// ═══════════════════════════════════════════════════

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
