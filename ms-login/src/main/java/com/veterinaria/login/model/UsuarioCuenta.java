package com.veterinaria.login.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ═══════════════════════════════════════════════════
// Entidad UsuarioCuenta -> tabla "usuarios"
// Representa una cuenta de acceso al sistema.
// El campo "rol" permite diferenciar permisos
// (ADMIN, VETERINARIO, RECEPCION).
// ═══════════════════════════════════════════════════

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    // Para este proyecto se guarda en texto (auth simple).
    // En un sistema real se cifraria con BCrypt.
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String rol;

    @Column(nullable = false)
    private Boolean activo;
}
