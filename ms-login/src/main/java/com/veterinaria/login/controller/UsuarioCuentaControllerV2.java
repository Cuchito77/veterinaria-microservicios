package com.veterinaria.login.controller;

import com.veterinaria.login.assembler.UsuarioModelAssembler;
import com.veterinaria.login.dto.UsuarioRequestDTO;
import com.veterinaria.login.dto.UsuarioResponseDTO;
import com.veterinaria.login.service.UsuarioCuentaService;
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
// Controller V2 de cuentas con HATEOAS.
// Ruta base: /api/v2/usuarios  (formato HAL_JSON)
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/v2/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios V2 (HATEOAS)", description = "API de cuentas con enlaces hipermedia HAL")
public class UsuarioCuentaControllerV2 {

    private final UsuarioCuentaService usuarioService;
    private final UsuarioModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar usuarios (HATEOAS)",
            description = "Lista las cuentas; cada una incluye sus enlaces y la coleccion incluye un enlace self")
    public CollectionModel<EntityModel<UsuarioResponseDTO>> obtenerTodos() {
        List<EntityModel<UsuarioResponseDTO>> usuarios = usuarioService.obtenerTodos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioCuentaControllerV2.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener usuario por ID (HATEOAS)",
            description = "Devuelve una cuenta con sus enlaces self y a la coleccion")
    public EntityModel<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return assembler.toModel(usuarioService.obtenerPorId(id));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear usuario (HATEOAS)",
            description = "Registra una cuenta y devuelve el recurso creado con sus enlaces")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO creado = usuarioService.guardar(dto);
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioCuentaControllerV2.class).obtenerPorId(creado.getId())).toUri())
                .body(assembler.toModel(creado));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar usuario (HATEOAS)",
            description = "Actualiza una cuenta y devuelve el recurso con sus enlaces")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO actualizado = usuarioService.actualizar(id, dto);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar usuario", description = "Elimina una cuenta por su ID")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
