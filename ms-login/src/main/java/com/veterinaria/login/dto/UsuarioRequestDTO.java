package com.veterinaria.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de entrada para crear/editar cuentas ────────
@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La password es obligatoria")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}
