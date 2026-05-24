package com.veterinaria.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de entrada para Dueno ───────────────────────
// Separamos el DTO de la entidad para validar datos de
// forma limpia (Bean Validation - JSR 380).
@Data @NoArgsConstructor @AllArgsConstructor
public class DuenoRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "El RUT no puede estar vacio")
    private String rut;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    // telefono es opcional
    private String telefono;
}
