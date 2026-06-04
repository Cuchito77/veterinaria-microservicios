package com.veterinaria.usuarios.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.veterinaria.usuarios.controller.DuenoControllerV2;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// ═══════════════════════════════════════════════════
// Assembler de HATEOAS para Dueno.
//   - self   -> GET /api/v2/duenos/{id}
//   - duenos -> GET /api/v2/duenos
// ═══════════════════════════════════════════════════

@Component
public class DuenoModelAssembler
        implements RepresentationModelAssembler<DuenoResponseDTO, EntityModel<DuenoResponseDTO>> {

    @Override
    public EntityModel<DuenoResponseDTO> toModel(DuenoResponseDTO dueno) {
        return EntityModel.of(dueno,
                linkTo(methodOn(DuenoControllerV2.class).obtenerPorId(dueno.getId())).withSelfRel(),
                linkTo(methodOn(DuenoControllerV2.class).obtenerTodos()).withRel("duenos"));
    }
}
