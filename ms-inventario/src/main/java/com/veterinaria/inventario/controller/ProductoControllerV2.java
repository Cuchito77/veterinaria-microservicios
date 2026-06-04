package com.veterinaria.inventario.controller;

import com.veterinaria.inventario.assembler.ProductoModelAssembler;
import com.veterinaria.inventario.dto.ProductoRequestDTO;
import com.veterinaria.inventario.dto.ProductoResponseDTO;
import com.veterinaria.inventario.service.ProductoService;
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
// Controller V2 de productos con HATEOAS.
// Ruta base: /api/v2/productos
// A diferencia del V1, cada respuesta incluye enlaces
// hipermedia (formato HAL_JSON), permitiendo al cliente
// navegar la API sin conocer las rutas de antemano.
// ═══════════════════════════════════════════════════

@RestController
@RequestMapping("/api/v2/productos")
@RequiredArgsConstructor
@Tag(name = "Productos V2 (HATEOAS)", description = "API de productos con enlaces hipermedia HAL")
public class ProductoControllerV2 {

    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar productos (HATEOAS)",
            description = "Lista los productos; cada uno incluye sus enlaces y la coleccion incluye un enlace self")
    public CollectionModel<EntityModel<ProductoResponseDTO>> obtenerTodos() {
        List<EntityModel<ProductoResponseDTO>> productos = productoService.obtenerTodos().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoControllerV2.class).obtenerTodos()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtener producto por ID (HATEOAS)",
            description = "Devuelve un producto con sus enlaces self y a la coleccion")
    public EntityModel<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return assembler.toModel(productoService.obtenerPorId(id));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear producto (HATEOAS)",
            description = "Registra un producto y devuelve el recurso creado con sus enlaces")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO creado = productoService.guardar(dto);
        return ResponseEntity
                .created(linkTo(methodOn(ProductoControllerV2.class).obtenerPorId(creado.getId())).toUri())
                .body(assembler.toModel(creado));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar producto (HATEOAS)",
            description = "Actualiza un producto y devuelve el recurso con sus enlaces")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO actualizado = productoService.actualizar(id, dto);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar producto",
            description = "Elimina un producto por su ID")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
