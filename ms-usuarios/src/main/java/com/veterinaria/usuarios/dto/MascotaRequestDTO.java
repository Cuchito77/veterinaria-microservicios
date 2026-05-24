package com.veterinaria.usuarios.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de entrada para Mascota ─────────────────────
@Data @NoArgsConstructor @AllArgsConstructor
public class MascotaRequestDTO {

    @NotBlank(message = "El nombre de la mascota no puede estar vacio")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    private String especie;

    // raza es opcional
    private String raza;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    @NotNull(message = "El duenoId es obligatorio")
    private Long duenoId;
}
