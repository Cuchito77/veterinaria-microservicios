package com.veterinaria.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

// ─── DTO de entrada para Producto ────────────────────
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos de entrada para crear o actualizar un producto del inventario")
public class ProductoRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Schema(description = "Nombre del producto", example = "Alimento para perro 10kg")
    private String nombre;

    @NotBlank(message = "La categoria es obligatoria")
    @Schema(description = "Categoria del producto", example = "Alimentos")
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio unitario del producto en pesos", example = "15990")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Cantidad de unidades disponibles en stock", example = "50")
    private Integer stock;
}
