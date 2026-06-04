package com.veterinaria.usuarios.controller;

import com.veterinaria.usuarios.dto.MascotaRequestDTO;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import com.veterinaria.usuarios.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

// ═══════════════════════════════════════════════════
// Controller de Mascotas. Ruta base: /api/mascotas
// Incluye un endpoint extra para que OTROS microservicios
// (ms-citas) puedan validar si una mascota existe:
//   GET /api/mascotas/{id}
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    @Operation(summary = "Listar mascotas", description = "Obtiene la lista completa de mascotas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente")
    })
    public ResponseEntity<List<MascotaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(mascotaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mascota por ID", description = "Devuelve una mascota según su identificador (usado por ms-citas para validar existencia)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota encontrada"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<MascotaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }

    // GET /api/mascotas/dueno/1 -> todas las mascotas de un dueno
    @GetMapping("/dueno/{duenoId}")
    @Operation(summary = "Listar mascotas por dueño", description = "Obtiene todas las mascotas asociadas a un dueño")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente")
    })
    public ResponseEntity<List<MascotaResponseDTO>> obtenerPorDueno(
            @PathVariable Long duenoId) {
        return ResponseEntity.ok(mascotaService.obtenerPorDueno(duenoId));
    }

    @PostMapping
    @Operation(summary = "Crear mascota", description = "Registra una nueva mascota asociada a un dueño")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mascota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la mascota inválidos")
    })
    public ResponseEntity<MascotaResponseDTO> crear(
            @Valid @RequestBody MascotaRequestDTO dto) {
        MascotaResponseDTO creada = mascotaService.guardar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota", description = "Actualiza los datos de una mascota existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<MascotaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody MascotaRequestDTO dto) {
        return ResponseEntity.ok(mascotaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mascota eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
