package com.veterinaria.usuarios.service;

import com.veterinaria.usuarios.dto.MascotaRequestDTO;
import com.veterinaria.usuarios.dto.MascotaResponseDTO;
import com.veterinaria.usuarios.exception.RecursoNoEncontradoException;
import com.veterinaria.usuarios.model.Dueno;
import com.veterinaria.usuarios.model.Mascota;
import com.veterinaria.usuarios.repository.DuenoRepository;
import com.veterinaria.usuarios.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════
// Capa SERVICE para Mascotas.
// Cada mascota debe estar asociada a un dueno existente,
// por eso este service tambien usa DuenoRepository.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class MascotaService {

    private static final Logger log = LoggerFactory.getLogger(MascotaService.class);

    private final MascotaRepository mascotaRepository;
    private final DuenoRepository duenoRepository;

    private MascotaResponseDTO mapToDTO(Mascota m) {
        return new MascotaResponseDTO(
                m.getId(), m.getNombre(), m.getEspecie(), m.getRaza(),
                m.getEdad(), m.getDueno().getId(), m.getDueno().getNombre());
    }

    public List<MascotaResponseDTO> obtenerTodas() {
        log.info("Listando todas las mascotas");
        return mascotaRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public MascotaResponseDTO obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Mascota no encontrada con id: " + id));
        return mapToDTO(mascota);
    }

    public List<MascotaResponseDTO> obtenerPorDueno(Long duenoId) {
        log.info("Listando mascotas del dueno id: {}", duenoId);
        return mascotaRepository.findByDuenoId(duenoId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public MascotaResponseDTO guardar(MascotaRequestDTO dto) {
        // Regla de negocio: la mascota debe pertenecer a un dueno existente
        Dueno dueno = duenoRepository.findById(dto.getDuenoId())
                .orElseThrow(() -> {
                    log.warn("No se puede crear mascota: dueno {} no existe", dto.getDuenoId());
                    return new RecursoNoEncontradoException(
                            "Dueno no encontrado con id: " + dto.getDuenoId());
                });
        Mascota mascota = new Mascota(null, dto.getNombre(), dto.getEspecie(),
                dto.getRaza(), dto.getEdad(), dueno);
        Mascota guardada = mascotaRepository.save(mascota);
        log.info("Mascota creada con id: {} para dueno: {}", guardada.getId(), dueno.getId());
        return mapToDTO(guardada);
    }

    public MascotaResponseDTO actualizar(Long id, MascotaRequestDTO dto) {
        Mascota existente = mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Mascota no encontrada con id: " + id));
        Dueno dueno = duenoRepository.findById(dto.getDuenoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Dueno no encontrado con id: " + dto.getDuenoId()));
        existente.setNombre(dto.getNombre());
        existente.setEspecie(dto.getEspecie());
        existente.setRaza(dto.getRaza());
        existente.setEdad(dto.getEdad());
        existente.setDueno(dueno);
        log.info("Mascota actualizada con id: {}", id);
        return mapToDTO(mascotaRepository.save(existente));
    }

    public void eliminar(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Mascota no encontrada con id: " + id);
        }
        mascotaRepository.deleteById(id);
        log.info("Mascota eliminada con id: {}", id);
    }
}
