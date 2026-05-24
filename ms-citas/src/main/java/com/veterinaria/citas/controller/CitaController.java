package com.veterinaria.citas.controller;

import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<CitaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(citaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    // GET /api/citas/mascota/1 -> todas las citas de una mascota
    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<CitaResponseDTO>> obtenerPorMascota(
            @PathVariable Long mascotaId) {
        return ResponseEntity.ok(citaService.obtenerPorMascota(mascotaId));
    }

    @PostMapping
    public ResponseEntity<CitaResponseDTO> crear(
            @Valid @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.status(201).body(citaService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.ok(citaService.actualizar(id, dto));
    }

    // PUT /api/citas/{id}/estado?valor=ATENDIDA
    @PutMapping("/{id}/estado")
    public ResponseEntity<CitaResponseDTO> cambiarEstado(
            @PathVariable Long id, @RequestParam String valor) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, valor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
