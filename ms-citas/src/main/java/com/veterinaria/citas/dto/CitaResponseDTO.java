package com.veterinaria.citas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos de una cita devueltos por el servicio")
public class CitaResponseDTO {

    @Schema(description = "Identificador unico de la cita", example = "1")
    private Long id;

    @Schema(description = "Identificador de la mascota a la que pertenece la cita", example = "1")
    private Long mascotaId;

    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String mascotaNombre;

    @Schema(description = "Motivo o razon de la cita medica", example = "Vacunacion anual")
    private String motivo;

    @Schema(description = "Fecha en que se agenda la cita", example = "2026-07-15")
    private LocalDate fecha;

    @Schema(description = "Hora en que se agenda la cita (HH:mm)", type = "string", example = "10:30")
    private LocalTime hora;

    @Schema(description = "Identificador del producto del inventario consumido en la cita (opcional)", example = "5")
    private Long productoId;

    @Schema(description = "Cantidad del producto consumido en la cita (opcional)", example = "2")
    private Integer cantidadProducto;

    @Schema(description = "Estado actual de la cita", example = "PENDIENTE")
    private String estado;
}
