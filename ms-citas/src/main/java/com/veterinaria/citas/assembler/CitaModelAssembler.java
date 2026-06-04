package com.veterinaria.citas.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.veterinaria.citas.controller.CitaControllerV2;
import com.veterinaria.citas.dto.CitaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// ═══════════════════════════════════════════════════
// Assembler de HATEOAS para Cita.
// Agrega enlaces de navegacion a cada cita:
//   - self  -> GET /api/v2/citas/{id}
//   - citas -> GET /api/v2/citas
// ═══════════════════════════════════════════════════

@Component
public class CitaModelAssembler
        implements RepresentationModelAssembler<CitaResponseDTO, EntityModel<CitaResponseDTO>> {

    @Override
    public EntityModel<CitaResponseDTO> toModel(CitaResponseDTO cita) {
        return EntityModel.of(cita,
                linkTo(methodOn(CitaControllerV2.class).obtenerPorId(cita.getId())).withSelfRel(),
                linkTo(methodOn(CitaControllerV2.class).obtenerTodas()).withRel("citas"));
    }
}
