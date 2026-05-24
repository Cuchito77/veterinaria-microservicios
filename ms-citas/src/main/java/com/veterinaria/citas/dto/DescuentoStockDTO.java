package com.veterinaria.citas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO para pedir descuento de stock a ms-inventario ─
// Se envia como body del PUT /api/productos/{id}/descontar
@Data @NoArgsConstructor @AllArgsConstructor
public class DescuentoStockDTO {
    private Integer cantidad;
}
