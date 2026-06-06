package com.veterinaria.login.config;

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
                        .title("API MICROSERVICIO LOGIN - VETERINARIA")
                        .version("1.0")
                        .description("DOCUMENTACIÓN DE LA API DEL MICROSERVICIO DE LOGIN (AUTENTICACIÓN)")
                )
                // ── JWT EN SWAGGER UI ────────────────────────
                // Declara el esquema "bearerAuth" y lo exige en
                // todos los endpoints: asi aparece el boton
                // "Authorize" en Swagger UI y "Try it out"
                // envia el token (antes respondia 403).
                // Nota: POST /api/auth/login es publico y sigue
                // funcionando sin token (permitAll en Security).
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
