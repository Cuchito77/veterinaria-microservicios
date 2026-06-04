package com.veterinaria.inventario.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.veterinaria.inventario.controller.ProductoControllerV2;
import com.veterinaria.inventario.dto.ProductoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// ═══════════════════════════════════════════════════
// Assembler de HATEOAS.
// Convierte un ProductoResponseDTO en un EntityModel
// agregando enlaces (links) de navegacion:
//   - self      -> al propio producto (GET /api/v2/productos/{id})
//   - productos -> a la lista completa  (GET /api/v2/productos)
// ═══════════════════════════════════════════════════

@Component
public class ProductoModelAssembler
        implements RepresentationModelAssembler<ProductoResponseDTO, EntityModel<ProductoResponseDTO>> {

    @Override
    public EntityModel<ProductoResponseDTO> toModel(ProductoResponseDTO producto) {
        return EntityModel.of(producto,
                linkTo(methodOn(ProductoControllerV2.class).obtenerPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoControllerV2.class).obtenerTodos()).withRel("productos"));
    }
}
