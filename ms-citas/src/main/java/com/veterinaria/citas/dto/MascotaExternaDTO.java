package com.veterinaria.citas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO externo: respuesta de ms-usuarios ───────────
// Solo mapeamos los campos que necesitamos de la mascota.
// @JsonIgnoreProperties evita errores si ms-usuarios
// devuelve campos extra que aqui no usamos.
@Data @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Datos de la mascota obtenidos desde el microservicio de usuarios")
public class MascotaExternaDTO {

    @Schema(description = "Identificador unico de la mascota", example = "1")
    private Long id;

    @Schema(description = "Nombre de la mascota", example = "Firulais")
    private String nombre;

    @Schema(description = "Especie de la mascota", example = "Perro")
    private String especie;
}
