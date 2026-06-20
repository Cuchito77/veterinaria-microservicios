package com.veterinaria.usuarios.config;

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
                        .title("API MICROSERVICIO USUARIOS - VETERINARIA")
                        .version("1.0")
                        .description("""
                                DOCUMENTACIÓN DE LA API DEL MICROSERVICIO DE USUARIOS (DUEÑOS Y MASCOTAS)

                                ---
                                ### 🔑 Cómo probar los endpoints (ver datos)
                                1. Pulsa el botón **Authorize** 🔒 (arriba a la derecha).
                                2. Pega este token de prueba (rol ADMIN) y pulsa Authorize:
                                ```
                                eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbCI6IkFETUlOIiwiZXhwIjo0MTAyNDQ0ODAwfQ.QlK9ZgClp1kGyv2UjM18eotKWFyuYE47LhERosFggVU
                                ```
                                3. Cierra el diálogo y usa **Try it out → Execute** en cualquier endpoint.

                                _El token queda guardado en el navegador: solo se autoriza una vez._
                                """)
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
