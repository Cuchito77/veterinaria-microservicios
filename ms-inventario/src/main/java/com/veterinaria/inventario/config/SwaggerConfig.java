package com.veterinaria.inventario.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("API MICROSERVICIO INVENTARIO - VETERINARIA")
                        .version("1.0")
                        .description("DOCUMENTACIÓN DE LA API DEL MICROSERVICIO DE INVENTARIO (PRODUCTOS)")
                )
                // ── JWT EN SWAGGER UI ────────────────────────
                // Declara el esquema "bearerAuth" y lo exige en
                // todos los endpoints: asi aparece el boton
                // "Authorize" en Swagger UI y "Try it out"
                // envia el token (antes respondia 403).
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
