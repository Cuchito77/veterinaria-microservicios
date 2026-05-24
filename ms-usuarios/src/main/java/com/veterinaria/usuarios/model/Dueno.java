package com.veterinaria.usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ═══════════════════════════════════════════════════
// Entidad Dueno -> tabla "duenos"
// Representa al cliente de la veterinaria, dueno de
// una o varias mascotas.
//
// Usamos ddl-auto=validate: Hibernate NO crea la tabla,
// solo valida que esta clase coincida con la tabla que
// Flyway creo en V1__crear_tabla_duenos.sql
// ═══════════════════════════════════════════════════

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "duenos")
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // RUT del dueno, unico en la BD (lo refuerza V1 con UNIQUE)
    @Column(nullable = false, length = 12, unique = true)
    private String rut;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;
}
