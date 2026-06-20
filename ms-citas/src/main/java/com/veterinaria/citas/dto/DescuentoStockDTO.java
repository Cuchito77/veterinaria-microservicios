package com.veterinaria.citas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO para pedir descuento de stock a ms-inventario ─
// Se envia como body del PUT /api/productos/{id}/descontar
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos para solicitar el descuento de stock de un producto en el inventario")
public class DescuentoStockDTO {

    @Schema(description = "Cantidad de unidades a descontar del stock", example = "2")
    private Integer cantidad;
}
