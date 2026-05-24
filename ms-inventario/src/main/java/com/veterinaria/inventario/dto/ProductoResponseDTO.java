package com.veterinaria.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private BigDecimal precio;
    private Integer stock;
}
