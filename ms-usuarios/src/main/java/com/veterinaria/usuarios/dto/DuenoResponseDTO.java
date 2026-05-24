package com.veterinaria.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DuenoResponseDTO {
    private Long id;
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
}
