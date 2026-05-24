package com.veterinaria.citas.client;

import com.veterinaria.citas.dto.DescuentoStockDTO;
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
// Cliente que consume ms-inventario mediante WebClient.
// Pide descontar stock de un producto cuando la cita
// lo consume.
// ═══════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
public class InventarioClient {

    private static final Logger log = LoggerFactory.getLogger(InventarioClient.class);

    private final WebClient inventarioWebClient;

    public void descontarStock(Long productoId, Integer cantidad) {
        log.info("Solicitando descuento de {} unidades del producto {} a ms-inventario",
                cantidad, productoId);
        try {
            inventarioWebClient.put()
                    .uri("/api/productos/{id}/descontar", productoId)
                    .bodyValue(new DescuentoStockDTO(cantidad))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.createException())
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(5))
                    .block();
            log.info("Stock descontado correctamente en ms-inventario");
        } catch (WebClientResponseException.NotFound e) {
            throw new RecursoNoEncontradoException(
                    "El producto con id " + productoId + " no existe en ms-inventario");
        } catch (WebClientResponseException.BadRequest e) {
            // ms-inventario devuelve 400 si no hay stock suficiente
            log.warn("ms-inventario rechazo el descuento: stock insuficiente");
            throw new RuntimeException(
                    "Stock insuficiente en ms-inventario para el producto " + productoId);
        } catch (Exception e) {
            log.error("Error al comunicarse con ms-inventario: {}", e.getMessage());
            throw new ComunicacionException(
                    "No se pudo contactar a ms-inventario. Verifique que este activo en el puerto 8083.");
        }
    }
}
