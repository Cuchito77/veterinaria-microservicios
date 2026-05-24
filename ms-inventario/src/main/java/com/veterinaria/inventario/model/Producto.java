package com.veterinaria.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

// ═══════════════════════════════════════════════════
// Entidad Producto -> tabla "productos"
// Representa un articulo del inventario de la veterinaria.
// ═══════════════════════════════════════════════════

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // Ej: Vacuna, Alimento, Medicamento, Accesorio
    @Column(nullable = false, length = 50)
    private String categoria;

    // BigDecimal -> tipo DECIMAL en MySQL (preciso para dinero)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;
}
