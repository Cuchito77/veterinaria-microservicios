package com.veterinaria.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de salida del login ─────────────────────────
// Devuelve si la autenticacion fue exitosa y el rol del
// usuario, para que el front decida que modulos mostrar.
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Resultado de la autenticacion del usuario")
public class LoginResponseDTO {
    @Schema(description = "Indica si la autenticacion fue exitosa", example = "true")
    private boolean autenticado;

    @Schema(description = "Mensaje descriptivo del resultado de la autenticacion", example = "Autenticacion exitosa")
    private String mensaje;

    @Schema(description = "Nombre de usuario autenticado", example = "admin")
    private String username;

    @Schema(description = "Rol del usuario autenticado", example = "ADMIN")
    private String rol;

    @Schema(description = "Token JWT emitido cuando la autenticacion es exitosa", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;   // JWT emitido cuando la autenticacion es exitosa (null si falla)
}
