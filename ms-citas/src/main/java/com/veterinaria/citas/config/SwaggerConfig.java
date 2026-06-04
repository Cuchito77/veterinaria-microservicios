package com.veterinaria.citas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("API MICROSERVICIO CITAS - VETERINARIA")
                        .version("1.0")
                        .description("DOCUMENTACIÓN DE LA API DEL MICROSERVICIO DE CITAS")
                );
    }
}
