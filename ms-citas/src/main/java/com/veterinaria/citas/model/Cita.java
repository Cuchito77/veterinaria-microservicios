package com.veterinaria.citas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

// ═══════════════════════════════════════════════════
// Entidad Cita -> tabla "citas"
//
// IMPORTANTE (autonomia de datos): esta tabla NO tiene
// FK hacia mascotas ni productos, porque viven en OTRAS
// bases de datos (otros microservicios). Solo guardamos
// el ID de referencia (mascota_id, producto_id) y la
// validacion de existencia se hace por WebClient.
// ═══════════════════════════════════════════════════

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referencia logica a la mascota (vive en ms-usuarios)
    @Column(name = "mascota_id", nullable = false)
    private Long mascotaId;

    // Guardamos el nombre que nos devolvio ms-usuarios,
    // para no tener que volver a consultarlo (proyeccion).
    @Column(name = "mascota_nombre", length = 80)
    private String mascotaNombre;

    @Column(nullable = false, length = 200)
    private String motivo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    // Referencia logica al producto (vive en ms-inventario).
    // Puede ser null si la cita no consume producto.
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "cantidad_producto")
    private Integer cantidadProducto;

    @Column(nullable = false, length = 20)
    private String estado;
}
