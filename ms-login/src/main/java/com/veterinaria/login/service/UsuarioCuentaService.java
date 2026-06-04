package com.veterinaria.login.service;

import com.veterinaria.login.dto.UsuarioRequestDTO;
import com.veterinaria.login.dto.UsuarioResponseDTO;
import com.veterinaria.login.exception.RecursoDuplicadoException;
import com.veterinaria.login.exception.RecursoNoEncontradoException;
import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════
// Gestion CRUD de cuentas de usuario.
// Las contrasenas se guardan cifradas con BCrypt.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class UsuarioCuentaService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioCuentaService.class);

    private final UsuarioCuentaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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
        // Regla de negocio: el username no se puede repetir -> 409 Conflict
        usuarioRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new RecursoDuplicadoException(
                    "Ya existe una cuenta con el username: " + dto.getUsername());
        });
        UsuarioCuenta cuenta = new UsuarioCuenta(null, dto.getUsername(),
                passwordEncoder.encode(dto.getPassword()), dto.getRol(), dto.getActivo());
        UsuarioCuenta guardada = usuarioRepository.save(cuenta);
        log.info("Cuenta creada con id: {}", guardada.getId());
        return mapToDTO(guardada);
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        UsuarioCuenta existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cuenta no encontrada con id: " + id));

        // Si cambia el username, verificar que no lo use OTRA cuenta -> 409 Conflict
        usuarioRepository.findByUsername(dto.getUsername())
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new RecursoDuplicadoException(
                            "Ya existe otra cuenta con el username: " + dto.getUsername());
                });

        existente.setUsername(dto.getUsername());
        existente.setPassword(passwordEncoder.encode(dto.getPassword()));
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
