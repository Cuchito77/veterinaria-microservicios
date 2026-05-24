package com.veterinaria.login.service;

import com.veterinaria.login.dto.UsuarioRequestDTO;
import com.veterinaria.login.dto.UsuarioResponseDTO;
import com.veterinaria.login.exception.RecursoNoEncontradoException;
import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════
// Gestion CRUD de cuentas de usuario.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class UsuarioCuentaService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioCuentaService.class);

    private final UsuarioCuentaRepository usuarioRepository;

    private UsuarioResponseDTO mapToDTO(UsuarioCuenta u) {
        return new UsuarioResponseDTO(u.getId(), u.getUsername(),
                u.getRol(), u.getActivo());
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        log.info("Listando todas las cuentas de usuario");
        return usuarioRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        UsuarioCuenta cuenta = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cuenta no encontrada con id: " + id));
        return mapToDTO(cuenta);
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        // Regla de negocio: el username no se puede repetir
        usuarioRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new RuntimeException("Ya existe una cuenta con el username: "
                    + dto.getUsername());
        });
        UsuarioCuenta cuenta = new UsuarioCuenta(null, dto.getUsername(),
                dto.getPassword(), dto.getRol(), dto.getActivo());
        UsuarioCuenta guardada = usuarioRepository.save(cuenta);
        log.info("Cuenta creada con id: {}", guardada.getId());
        return mapToDTO(guardada);
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        UsuarioCuenta existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cuenta no encontrada con id: " + id));
        existente.setUsername(dto.getUsername());
        existente.setPassword(dto.getPassword());
        existente.setRol(dto.getRol());
        existente.setActivo(dto.getActivo());
        log.info("Cuenta actualizada con id: {}", id);
        return mapToDTO(usuarioRepository.save(existente));
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Cuenta no encontrada con id: " + id);
        }
        usuarioRepository.deleteById(id);
        log.info("Cuenta eliminada con id: {}", id);
    }
}
