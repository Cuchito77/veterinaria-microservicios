package com.veterinaria.citas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

// ═══════════════════════════════════════════════════
// Configuracion de los WebClient hacia los otros MS.
// Al estar protegidos con JWT, ms-citas debe PROPAGAR
// el token del usuario (header Authorization) en cada
// llamada interna; si no, ms-usuarios/ms-inventario
// responderian 401.
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
        return WebClient.builder()
                .baseUrl(usuariosUrl)
                .filter(propagarToken())
                .build();
    }

    // WebClient apuntando a ms-inventario (puerto 8083)
    @Bean
    public WebClient inventarioWebClient() {
        return WebClient.builder()
                .baseUrl(inventarioUrl)
                .filter(propagarToken())
                .build();
    }

    // Filtro que copia el header Authorization de la peticion
    // entrante (la del usuario) a la peticion saliente hacia el otro MS.
    private ExchangeFilterFunction propagarToken() {
        return (request, next) -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String authHeader = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && !authHeader.isBlank()) {
                    ClientRequest nuevaRequest = ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, authHeader)
                            .build();
                    return next.exchange(nuevaRequest);
                }
            }
            return next.exchange(request);
        };
    }
}
