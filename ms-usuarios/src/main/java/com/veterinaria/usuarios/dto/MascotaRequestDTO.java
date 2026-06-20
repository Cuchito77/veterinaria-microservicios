package com.veterinaria.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de entrada para Mascota ─────────────────────
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos necesarios para crear o actualizar una mascota")
public class MascotaRequestDTO {

    @NotBlank(message = "El nombre de la mascota no puede estar vacio")
    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    @Schema(description = "Especie de la mascota", example = "Perro")
    private String especie;

    // raza es opcional
    @Schema(description = "Raza de la mascota (opcional)", example = "Labrador")
    private String raza;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Schema(description = "Edad de la mascota en anos", example = "3")
    private Integer edad;

    @NotNull(message = "El duenoId es obligatorio")
    @Schema(description = "ID del dueno al que pertenece la mascota", example = "1")
    private Long duenoId;
}
