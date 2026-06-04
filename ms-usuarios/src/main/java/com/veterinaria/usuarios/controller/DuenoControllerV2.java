package com.veterinaria.usuarios.controller;

import com.veterinaria.usuarios.assembler.DuenoModelAssembler;
import com.veterinaria.usuarios.dto.DuenoRequestDTO;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import com.veterinaria.usuarios.service.DuenoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// ═══════════════════════════════════════════════════
// Controller V2 de duenos con HATEOAS.
// Ruta base: /api/v2/duenos  (formato HAL_JSON)
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/v2/duenos")
@RequiredArgsConstructor
@Tag(name = "Duenos V2 (HATEOAS)", description = "API de duenos con enlaces hipermedia HAL")
public class DuenoControllerV2 {

    private final DuenoService duenoService;
    private final DuenoModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar duenos (HATEOAS)",
            description = "Lista los duenos; cada uno incluye sus enlaces y la coleccion incluye un enlace self")
    public CollectionModel<EntityModel<DuenoResponseDTO>> obtenerTodos() {
        List<EntityModel<DuenoResponseDTO>> duenos = duenoService.obtenerTodos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(duenos,
                linkTo(methodOn(DuenoControllerV2.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener dueno por ID (HATEOAS)",
            description = "Devuelve un dueno con sus enlaces self y a la coleccion")
    public EntityModel<DuenoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return assembler.toModel(duenoService.obtenerPorId(id));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear dueno (HATEOAS)",
            description = "Registra un dueno y devuelve el recurso creado con sus enlaces")
    public ResponseEntity<EntityModel<DuenoResponseDTO>> crear(@Valid @RequestBody DuenoRequestDTO dto) {
        DuenoResponseDTO creado = duenoService.guardar(dto);
        return ResponseEntity
                .created(linkTo(methodOn(DuenoControllerV2.class).obtenerPorId(creado.getId())).toUri())
                .body(assembler.toModel(creado));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar dueno (HATEOAS)",
            description = "Actualiza un dueno y devuelve el recurso con sus enlaces")
    public ResponseEntity<EntityModel<DuenoResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody DuenoRequestDTO dto) {
        DuenoResponseDTO actualizado = duenoService.actualizar(id, dto);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar dueno", description = "Elimina un dueno por su ID")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        duenoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
