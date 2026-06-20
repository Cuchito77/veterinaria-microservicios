package com.veterinaria.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos de un producto del inventario")
public class ProductoResponseDTO {
    @Schema(description = "Identificador unico del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Alimento para perro 10kg")
    private String nombre;

    @Schema(description = "Categoria del producto", example = "Alimentos")
    private String categoria;

    @Schema(description = "Precio unitario del producto en pesos", example = "15990")
    private BigDecimal precio;

    @Schema(description = "Cantidad de unidades disponibles en stock", example = "50")
    private Integer stock;
}
