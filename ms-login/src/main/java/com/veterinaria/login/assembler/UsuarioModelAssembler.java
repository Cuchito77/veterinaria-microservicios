package com.veterinaria.login.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.veterinaria.login.controller.UsuarioCuentaControllerV2;
import com.veterinaria.login.dto.UsuarioResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// ═══════════════════════════════════════════════════
// Assembler de HATEOAS para UsuarioCuenta.
//   - self     -> GET /api/v2/usuarios/{id}
//   - usuarios -> GET /api/v2/usuarios
// ═══════════════════════════════════════════════════

@Component
public class UsuarioModelAssembler
        implements RepresentationModelAssembler<UsuarioResponseDTO, EntityModel<UsuarioResponseDTO>> {

    @Override
    public EntityModel<UsuarioResponseDTO> toModel(UsuarioResponseDTO usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioCuentaControllerV2.class).obtenerPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioCuentaControllerV2.class).obtenerTodos()).withRel("usuarios"));
    }
}
