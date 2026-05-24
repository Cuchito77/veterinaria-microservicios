package com.veterinaria.citas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class CitaResponseDTO {
    private Long id;
    private Long mascotaId;
    private String mascotaNombre;
    private String motivo;
    private LocalDate fecha;
    private LocalTime hora;
    private Long productoId;
    private Integer cantidadProducto;
    private String estado;
}
