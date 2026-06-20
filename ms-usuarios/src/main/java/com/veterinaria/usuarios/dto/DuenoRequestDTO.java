package com.veterinaria.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ─── DTO de entrada para Dueno ───────────────────────
// Separamos el DTO de la entidad para validar datos de
// forma limpia (Bean Validation - JSR 380).
@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos necesarios para crear o actualizar un dueno")
public class DuenoRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    @Schema(description = "Nombre completo del dueno", example = "Juan Perez Soto")
    private String nombre;

    @NotBlank(message = "El RUT no puede estar vacio")
    @Schema(description = "RUT del dueno (con digito verificador)", example = "12.345.678-9")
    private String rut;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email debe tener un formato valido")
    @Schema(description = "Correo electronico de contacto", example = "juan.perez@gmail.com")
    private String email;

    // telefono es opcional
    @Schema(description = "Telefono de contacto (opcional)", example = "+56912345678")
    private String telefono;
}
