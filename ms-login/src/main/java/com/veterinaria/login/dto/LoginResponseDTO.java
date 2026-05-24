package com.veterinaria.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de salida del login ─────────────────────────
// Devuelve si la autenticacion fue exitosa y el rol del
// usuario, para que el front decida que modulos mostrar.
@Data @NoArgsConstructor @AllArgsConstructor
public class LoginResponseDTO {
    private boolean autenticado;
    private String mensaje;
    private String username;
    private String rol;
}
