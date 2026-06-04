package com.veterinaria.usuarios.controller;

import com.veterinaria.usuarios.assembler.MascotaModelAssembler;
import com.veterinaria.usuarios.dto.MascotaRequestDTO;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import com.veterinaria.usuarios.service.MascotaService;
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
// Controller V2 de mascotas con HATEOAS.
// Ruta base: /api/v2/mascotas  (formato HAL_JSON)
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/v2/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas V2 (HATEOAS)", description = "API de mascotas con enlaces hipermedia HAL")
public class MascotaControllerV2 {

    private final MascotaService mascotaService;
    private final MascotaModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar mascotas (HATEOAS)",
            description = "Lista las mascotas; cada una incluye sus enlaces y la coleccion incluye un enlace self")
    public CollectionModel<EntityModel<MascotaResponseDTO>> obtenerTodas() {
        List<EntityModel<MascotaResponseDTO>> mascotas = mascotaService.obtenerTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(mascotas,
                linkTo(methodOn(MascotaControllerV2.class).obtenerTodas()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener mascota por ID (HATEOAS)",
            description = "Devuelve una mascota con sus enlaces self y a la coleccion")
    public EntityModel<MascotaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return assembler.toModel(mascotaService.obtenerPorId(id));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear mascota (HATEOAS)",
            description = "Registra una mascota y devuelve el recurso creado con sus enlaces")
    public ResponseEntity<EntityModel<MascotaResponseDTO>> crear(@Valid @RequestBody MascotaRequestDTO dto) {
        MascotaResponseDTO creada = mascotaService.guardar(dto);
        return ResponseEntity
                .created(linkTo(methodOn(MascotaControllerV2.class).obtenerPorId(creada.getId())).toUri())
                .body(assembler.toModel(creada));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar mascota (HATEOAS)",
            description = "Actualiza una mascota y devuelve el recurso con sus enlaces")
    public ResponseEntity<EntityModel<MascotaResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody MascotaRequestDTO dto) {
        MascotaResponseDTO actualizada = mascotaService.actualizar(id, dto);
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota por su ID")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
