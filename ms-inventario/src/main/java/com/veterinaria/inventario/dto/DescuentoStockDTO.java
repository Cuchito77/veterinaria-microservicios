package com.veterinaria.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO para descontar stock ────────────────────────
// Lo usa ms-citas cuando una cita consume un producto.
// PUT /api/productos/{id}/descontar  body: { "cantidad": 2 }
@Data @NoArgsConstructor @AllArgsConstructor
public class DescuentoStockDTO {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad a descontar debe ser al menos 1")
    private Integer cantidad;
}
