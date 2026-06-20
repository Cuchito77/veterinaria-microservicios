package com.veterinaria.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de entrada para el login ────────────────────
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Credenciales para iniciar sesion")
public class LoginRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Nombre de usuario para autenticarse", example = "admin")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    @Schema(description = "Contrasena del usuario", example = "Admin123")
    private String password;
}
