package com.veterinaria.usuarios.service;

import com.veterinaria.usuarios.dto.DuenoRequestDTO;
import com.veterinaria.usuarios.dto.DuenoResponseDTO;
import com.veterinaria.usuarios.exception.RecursoNoEncontradoException;
import com.veterinaria.usuarios.model.Dueno;
import com.veterinaria.usuarios.repository.DuenoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════
// Capa SERVICE: concentra la logica de negocio de los
// duenos. El controller nunca habla directo con el
// repository, siempre pasa por aqui.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class DuenoService {

    private static final Logger log = LoggerFactory.getLogger(DuenoService.class);

    private final DuenoRepository duenoRepository;

    // Conversion entidad -> DTO de salida
    private DuenoResponseDTO mapToDTO(Dueno d) {
        return new DuenoResponseDTO(d.getId(), d.getNombre(),
                d.getRut(), d.getEmail(), d.getTelefono());
    }

    public List<DuenoResponseDTO> obtenerTodos() {
        log.info("Listando todos los duenos");
        return duenoRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public DuenoResponseDTO obtenerPorId(Long id) {
        Dueno dueno = duenoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Dueno no encontrado con id: {}", id);
                    return new RecursoNoEncontradoException(
                            "Dueno no encontrado con id: " + id);
                });
        return mapToDTO(dueno);
    }

    public DuenoResponseDTO guardar(DuenoRequestDTO dto) {
        // Regla de negocio: no se permite RUT duplicado
        duenoRepository.findByRut(dto.getRut()).ifPresent(d -> {
            log.warn("Intento de crear dueno con RUT duplicado: {}", dto.getRut());
            throw new RuntimeException("Ya existe un dueno con el RUT: " + dto.getRut());
        });
        Dueno dueno = new Dueno(null, dto.getNombre(),
                dto.getRut(), dto.getEmail(), dto.getTelefono());
        Dueno guardado = duenoRepository.save(dueno);
        log.info("Dueno creado con id: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    public DuenoResponseDTO actualizar(Long id, DuenoRequestDTO dto) {
        Dueno existente = duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Dueno no encontrado con id: " + id));
        existente.setNombre(dto.getNombre());
        existente.setRut(dto.getRut());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());
        log.info("Dueno actualizado con id: {}", id);
        return mapToDTO(duenoRepository.save(existente));
    }

    public void eliminar(Long id) {
        if (!duenoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Dueno no encontrado con id: " + id);
        }
        duenoRepository.deleteById(id);
        log.info("Dueno eliminado con id: {}", id);
    }
}
