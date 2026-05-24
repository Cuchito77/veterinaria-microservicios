package com.veterinaria.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de salida de cuentas ────────────────────────
// Nunca devolvemos la password en la respuesta.
@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String rol;
    private Boolean activo;
}
