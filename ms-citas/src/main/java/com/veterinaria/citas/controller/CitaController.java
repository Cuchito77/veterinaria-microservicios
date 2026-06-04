package com.veterinaria.citas.controller;

import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.service.CitaService;
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
// Controller de citas. Ruta base: /api/citas
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    @Operation(summary = "Listar citas", description = "Obtiene la lista completa de citas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente")
    })
    public ResponseEntity<List<CitaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(citaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por ID", description = "Devuelve una cita según su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita encontrada"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<CitaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    // GET /api/citas/mascota/1 -> todas las citas de una mascota
    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Listar citas por mascota", description = "Obtiene todas las citas asociadas a una mascota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente")
    })
    public ResponseEntity<List<CitaResponseDTO>> obtenerPorMascota(
            @PathVariable Long mascotaId) {
        return ResponseEntity.ok(citaService.obtenerPorMascota(mascotaId));
    }

    @PostMapping
    @Operation(summary = "Crear cita", description = "Registra una nueva cita (valida mascota en ms-usuarios y descuenta stock en ms-inventario)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cita creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la cita inválidos")
    })
    public ResponseEntity<CitaResponseDTO> crear(
            @Valid @RequestBody CitaRequestDTO dto) {
        CitaResponseDTO creada = citaService.guardar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cita", description = "Actualiza los datos de una cita existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cita actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<CitaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.ok(citaService.actualizar(id, dto));
    }

    // PUT /api/citas/{id}/estado?valor=ATENDIDA
    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de la cita", description = "Cambia el estado de una cita (ej: PENDIENTE, ATENDIDA, CANCELADA)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la cita actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<CitaResponseDTO> cambiarEstado(
            @PathVariable Long id, @RequestParam String valor) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, valor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cita", description = "Elimina una cita por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cita eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
