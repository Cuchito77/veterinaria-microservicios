package com.veterinaria.citas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO externo: respuesta de ms-usuarios ───────────
// Solo mapeamos los campos que necesitamos de la mascota.
// @JsonIgnoreProperties evita errores si ms-usuarios
// devuelve campos extra que aqui no usamos.
@Data @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MascotaExternaDTO {
    private Long id;
    private String nombre;
    private String especie;
}
