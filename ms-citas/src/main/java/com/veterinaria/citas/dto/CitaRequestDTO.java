package com.veterinaria.citas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

// ─── DTO de entrada para Cita ────────────────────────
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos necesarios para crear o actualizar una cita")
public class CitaRequestDTO {

    @NotNull(message = "El mascotaId es obligatorio")
    @Schema(description = "Identificador de la mascota a la que pertenece la cita", example = "1")
    private Long mascotaId;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    @Schema(description = "Motivo o razon de la cita medica", example = "Vacunacion anual")
    private String motivo;

    @NotNull(message = "La fecha es obligatoria")
    @Schema(description = "Fecha en que se agenda la cita", example = "2026-07-15")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Schema(description = "Hora en que se agenda la cita (HH:mm)", type = "string", example = "10:30")
    private LocalTime hora;

    // Opcionales: solo si la cita consume un producto del inventario
    @Schema(description = "Identificador del producto del inventario consumido en la cita (opcional)", example = "5")
    private Long productoId;

    @Schema(description = "Cantidad del producto consumido en la cita (opcional)", example = "2")
    private Integer cantidadProducto;
}
