package com.veterinaria.citas.client;

import com.veterinaria.citas.dto.MascotaExternaDTO;
import com.veterinaria.citas.exception.ComunicacionException;
import com.veterinaria.citas.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;

// ═══════════════════════════════════════════════════
// Cliente que consume ms-usuarios mediante WebClient.
// Su unica responsabilidad: traer una mascota por id
// para validar que existe antes de crear una cita.
// ═══════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
public class UsuariosClient {

    private static final Logger log = LoggerFactory.getLogger(UsuariosClient.class);

    private final WebClient usuariosWebClient;

    public MascotaExternaDTO obtenerMascota(Long mascotaId) {
        log.info("Consultando mascota {} en ms-usuarios", mascotaId);
        try {
            return usuariosWebClient.get()
                    .uri("/api/mascotas/{id}", mascotaId)
                    .retrieve()
                    // Si ms-usuarios responde 404, la mascota no existe
                    .onStatus(HttpStatusCode::is4xxClientError, resp -> {
                        log.warn("ms-usuarios respondio error para mascota {}", mascotaId);
                        return resp.createException();
                    })
                    .bodyToMono(MascotaExternaDTO.class)
                    // timeout: si ms-usuarios no responde en 5s, falla
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new RecursoNoEncontradoException(
                    "La mascota con id " + mascotaId + " no existe en ms-usuarios");
        } catch (Exception e) {
            log.error("Error al comunicarse con ms-usuarios: {}", e.getMessage());
            throw new ComunicacionException(
                    "No se pudo contactar a ms-usuarios. Verifique que este activo en el puerto 8081.");
        }
    }
}
