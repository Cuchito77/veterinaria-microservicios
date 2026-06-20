package com.veterinaria.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Datos de un dueno devueltos por la API")
public class DuenoResponseDTO {

    @Schema(description = "Identificador unico del dueno", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del dueno", example = "Juan Perez Soto")
    private String nombre;

    @Schema(description = "RUT del dueno (con digito verificador)", example = "12.345.678-9")
    private String rut;

    @Schema(description = "Correo electronico de contacto", example = "juan.perez@gmail.com")
    private String email;

    @Schema(description = "Telefono de contacto", example = "+56912345678")
    private String telefono;
}
