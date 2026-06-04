package com.veterinaria.citas.controller;

import com.veterinaria.citas.assembler.CitaModelAssembler;
import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.service.CitaService;
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
// Controller V2 de citas con HATEOAS.
// Ruta base: /api/v2/citas  (formato HAL_JSON)
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/v2/citas")
@RequiredArgsConstructor
@Tag(name = "Citas V2 (HATEOAS)", description = "API de citas con enlaces hipermedia HAL")
public class CitaControllerV2 {

    private final CitaService citaService;
    private final CitaModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar citas (HATEOAS)",
            description = "Lista las citas; cada una incluye sus enlaces y la coleccion incluye un enlace self")
    public CollectionModel<EntityModel<CitaResponseDTO>> obtenerTodas() {
        List<EntityModel<CitaResponseDTO>> citas = citaService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(citas,
                linkTo(methodOn(CitaControllerV2.class).obtenerTodas()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener cita por ID (HATEOAS)",
            description = "Devuelve una cita con sus enlaces self y a la coleccion")
    public EntityModel<CitaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return assembler.toModel(citaService.obtenerPorId(id));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear cita (HATEOAS)",
            description = "Registra una cita y devuelve el recurso creado con sus enlaces")
    public ResponseEntity<EntityModel<CitaResponseDTO>> crear(@Valid @RequestBody CitaRequestDTO dto) {
        CitaResponseDTO creada = citaService.guardar(dto);
        return ResponseEntity
                .created(linkTo(methodOn(CitaControllerV2.class).obtenerPorId(creada.getId())).toUri())
                .body(assembler.toModel(creada));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar cita (HATEOAS)",
            description = "Actualiza una cita y devuelve el recurso con sus enlaces")
    public ResponseEntity<EntityModel<CitaResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CitaRequestDTO dto) {
        CitaResponseDTO actualizada = citaService.actualizar(id, dto);
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar cita", description = "Elimina una cita por su ID")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
