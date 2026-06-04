package com.veterinaria.usuarios.controller;

import com.veterinaria.usuarios.dto.DuenoRequestDTO;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import com.veterinaria.usuarios.service.DuenoService;
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
    @Operation(summary = "Listar dueños", description = "Obtiene la lista completa de dueños registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de dueños obtenida exitosamente")
    })
    public ResponseEntity<List<DuenoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(duenoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener dueño por ID", description = "Devuelve un dueño según su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dueño encontrado"),
            @ApiResponse(responseCode = "404", description = "Dueño no encontrado")
    })
    public ResponseEntity<DuenoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(duenoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear dueño", description = "Registra un nuevo dueño")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dueño creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos del dueño inválidos")
    })
    public ResponseEntity<DuenoResponseDTO> crear(
            @Valid @RequestBody DuenoRequestDTO dto) {
        DuenoResponseDTO creado = duenoService.guardar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar dueño", description = "Actualiza los datos de un dueño existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dueño actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Dueño no encontrado")
    })
    public ResponseEntity<DuenoResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody DuenoRequestDTO dto) {
        return ResponseEntity.ok(duenoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar dueño", description = "Elimina un dueño por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dueño eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Dueño no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        duenoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
