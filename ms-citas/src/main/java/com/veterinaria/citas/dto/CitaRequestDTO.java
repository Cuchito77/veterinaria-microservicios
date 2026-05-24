package com.veterinaria.citas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

// ─── DTO de entrada para Cita ────────────────────────
@Data @NoArgsConstructor @AllArgsConstructor
public class CitaRequestDTO {

    @NotNull(message = "El mascotaId es obligatorio")
    private Long mascotaId;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    private String motivo;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    // Opcionales: solo si la cita consume un producto del inventario
    private Long productoId;
    private Integer cantidadProducto;
}
