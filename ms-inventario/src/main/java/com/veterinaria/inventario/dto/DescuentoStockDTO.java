package com.veterinaria.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO para descontar stock ────────────────────────
// Lo usa ms-citas cuando una cita consume un producto.
// PUT /api/productos/{id}/descontar  body: { "cantidad": 2 }
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos para descontar stock de un producto del inventario")
public class DescuentoStockDTO {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad a descontar debe ser al menos 1")
    @Schema(description = "Cantidad de unidades a descontar del stock", example = "2")
    private Integer cantidad;
}
