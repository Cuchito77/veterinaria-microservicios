package com.veterinaria.citas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// ═══════════════════════════════════════════════════
// Configuracion de los WebClient hacia los otros MS.
// En Spring Boot 4.x el WebClient.Builder ya no se
// autoconfigura, por eso creamos el WebClient
// directamente con WebClient.create(url).
// ═══════════════════════════════════════════════════

@Configuration
public class WebClientConfig {

    @Value("${ms.usuarios.url}")
    private String usuariosUrl;

    @Value("${ms.inventario.url}")
    private String inventarioUrl;

    // WebClient apuntando a ms-usuarios (puerto 8081)
    @Bean
    public WebClient usuariosWebClient() {
        return WebClient.create(usuariosUrl);
    }

    // WebClient apuntando a ms-inventario (puerto 8083)
    @Bean
    public WebClient inventarioWebClient() {
        return WebClient.create(inventarioUrl);
    }
}