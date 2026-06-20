package com.veterinaria.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de salida de cuentas ────────────────────────
// Nunca devolvemos la password en la respuesta.
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos de una cuenta de usuario")
public class UsuarioResponseDTO {
    @Schema(description = "Identificador unico del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario de la cuenta", example = "admin")
    private String username;

    @Schema(description = "Rol asignado al usuario", example = "ADMIN")
    private String rol;

    @Schema(description = "Indica si la cuenta esta activa", example = "true")
    private Boolean activo;
}
