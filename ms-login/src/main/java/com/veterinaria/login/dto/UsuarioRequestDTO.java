package com.veterinaria.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de entrada para crear/editar cuentas ────────
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos para crear o editar una cuenta de usuario")
public class UsuarioRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    @Schema(description = "Nombre de usuario de la cuenta", example = "admin")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    @Schema(description = "Contrasena del usuario", example = "Admin123")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Schema(description = "Rol asignado al usuario", example = "ADMIN")
    private String rol;

    @NotNull(message = "El estado activo es obligatorio")
    @Schema(description = "Indica si la cuenta esta activa", example = "true")
    private Boolean activo;
}
