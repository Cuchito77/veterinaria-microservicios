package com.veterinaria.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos de una mascota devueltos por la API")
public class MascotaResponseDTO {

    @Schema(description = "Identificador unico de la mascota", example = "1")
    private Long id;

    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombre;

    @Schema(description = "Especie de la mascota", example = "Perro")
    private String especie;

    @Schema(description = "Raza de la mascota", example = "Labrador")
    private String raza;

    @Schema(description = "Edad de la mascota en anos", example = "3")
    private Integer edad;

    @Schema(description = "ID del dueno al que pertenece", example = "1")
    private Long duenoId;

    @Schema(description = "Nombre del dueno al que pertenece", example = "Juan Perez Soto")
    private String duenoNombre;
}
