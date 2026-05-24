package com.veterinaria.usuarios.controller;

import com.veterinaria.usuarios.dto.MascotaRequestDTO;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import com.veterinaria.usuarios.service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<MascotaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(mascotaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }

    // GET /api/mascotas/dueno/1 -> todas las mascotas de un dueno
    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<MascotaResponseDTO>> obtenerPorDueno(
            @PathVariable Long duenoId) {
        return ResponseEntity.ok(mascotaService.obtenerPorDueno(duenoId));
    }

    @PostMapping
    public ResponseEntity<MascotaResponseDTO> crear(
            @Valid @RequestBody MascotaRequestDTO dto) {
        return ResponseEntity.status(201).body(mascotaService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody MascotaRequestDTO dto) {
        return ResponseEntity.ok(mascotaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
