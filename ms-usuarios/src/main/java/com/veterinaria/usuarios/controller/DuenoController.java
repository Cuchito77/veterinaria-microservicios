package com.veterinaria.usuarios.controller;

import com.veterinaria.usuarios.dto.DuenoRequestDTO;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import com.veterinaria.usuarios.service.DuenoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// ═══════════════════════════════════════════════════
// Capa CONTROLLER: solo orquesta las solicitudes REST.
// No tiene logica de negocio, delega todo al service.
// Ruta base: /api/duenos
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/duenos")
@RequiredArgsConstructor
public class DuenoController {

    private final DuenoService duenoService;

    @GetMapping
    public ResponseEntity<List<DuenoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(duenoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DuenoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(duenoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DuenoResponseDTO> crear(
            @Valid @RequestBody DuenoRequestDTO dto) {
        return ResponseEntity.status(201).body(duenoService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DuenoResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody DuenoRequestDTO dto) {
        return ResponseEntity.ok(duenoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        duenoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
