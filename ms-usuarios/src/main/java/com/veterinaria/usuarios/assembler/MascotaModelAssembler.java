package com.veterinaria.usuarios.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.veterinaria.usuarios.controller.MascotaControllerV2;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// ═══════════════════════════════════════════════════
// Assembler de HATEOAS para Mascota.
//   - self     -> GET /api/v2/mascotas/{id}
//   - mascotas -> GET /api/v2/mascotas
// ═══════════════════════════════════════════════════

@Component
public class MascotaModelAssembler
        implements RepresentationModelAssembler<MascotaResponseDTO, EntityModel<MascotaResponseDTO>> {

    @Override
    public EntityModel<MascotaResponseDTO> toModel(MascotaResponseDTO mascota) {
        return EntityModel.of(mascota,
                linkTo(methodOn(MascotaControllerV2.class).obtenerPorId(mascota.getId())).withSelfRel(),
                linkTo(methodOn(MascotaControllerV2.class).obtenerTodas()).withRel("mascotas"));
    }
}
