package com.veterinaria.usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ═══════════════════════════════════════════════════
// Entidad Mascota -> tabla "mascotas"
// Cada mascota pertenece a un Dueno (relacion ManyToOne).
// La FK dueno_id la define V2__crear_tabla_mascotas.sql
// ═══════════════════════════════════════════════════

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    // Ej: Perro, Gato, Conejo
    @Column(nullable = false, length = 50)
    private String especie;

    @Column(length = 60)
    private String raza;

    @Column(nullable = false)
    private Integer edad;

    // Muchas mascotas pertenecen a un dueno
    @ManyToOne
    @JoinColumn(name = "dueno_id", nullable = false)
    private Dueno dueno;
}
