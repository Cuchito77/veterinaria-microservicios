package com.veterinaria.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ═══════════════════════════════════════════════════
// MS-INVENTARIO · InventarioApplication.java
// Gestiona los productos de la veterinaria (vacunas,
// alimentos, medicamentos, etc.) y su stock.
// Expone un endpoint para descontar stock que es
// consumido por ms-citas.
// ═══════════════════════════════════════════════════

@SpringBootApplication
public class InventarioApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }
}
