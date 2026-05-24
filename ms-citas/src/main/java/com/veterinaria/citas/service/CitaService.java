package com.veterinaria.citas.service;

import com.veterinaria.citas.client.InventarioClient;
import com.veterinaria.citas.client.UsuariosClient;
import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.dto.MascotaExternaDTO;
import com.veterinaria.citas.exception.RecursoNoEncontradoException;
import com.veterinaria.citas.model.Cita;
import com.veterinaria.citas.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// ═══════════════════════════════════════════════════
// CitaService: corazon del proyecto.
// Aqui se ve la COMUNICACION ENTRE MICROSERVICIOS:
//
//  Al crear una cita:
//   1) Llama a ms-usuarios para validar que la mascota
//      exista (UsuariosClient).
//   2) Si la cita usa un producto, llama a ms-inventario
//      para descontar stock (InventarioClient).
//   3) Solo entonces guarda la cita en su propia BD.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    private final CitaRepository citaRepository;
    private final UsuariosClient usuariosClient;
    private final InventarioClient inventarioClient;

    private CitaResponseDTO mapToDTO(Cita c) {
        return new CitaResponseDTO(c.getId(), c.getMascotaId(), c.getMascotaNombre(),
                c.getMotivo(), c.getFecha(), c.getHora(),
                c.getProductoId(), c.getCantidadProducto(), c.getEstado());
    }

    public List<CitaResponseDTO> obtenerTodas() {
        log.info("Listando todas las citas");
        return citaRepository.findAll().stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public CitaResponseDTO obtenerPorId(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cita no encontrada con id: " + id));
        return mapToDTO(cita);
    }

    public List<CitaResponseDTO> obtenerPorMascota(Long mascotaId) {
        log.info("Listando citas de la mascota id: {}", mascotaId);
        return citaRepository.findByMascotaId(mascotaId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    public CitaResponseDTO guardar(CitaRequestDTO dto) {
        // ── PASO 1: validar la mascota contra ms-usuarios ──
        MascotaExternaDTO mascota = usuariosClient.obtenerMascota(dto.getMascotaId());
        log.info("Mascota validada: {} (id {})", mascota.getNombre(), mascota.getId());

        // ── PASO 2: si hay producto, descontar stock en ms-inventario ──
        if (dto.getProductoId() != null) {
            int cantidad = (dto.getCantidadProducto() != null)
                    ? dto.getCantidadProducto() : 1;
            inventarioClient.descontarStock(dto.getProductoId(), cantidad);
        }

        // ── PASO 3: guardar la cita en la BD propia ──
        Cita cita = new Cita(null,
                mascota.getId(),
                mascota.getNombre(),   // proyeccion del nombre traido de ms-usuarios
                dto.getMotivo(),
                dto.getFecha(),
                dto.getHora(),
                dto.getProductoId(),
                dto.getCantidadProducto(),
                "PROGRAMADA");
        Cita guardada = citaRepository.save(cita);
        log.info("Cita creada con id: {} para mascota {}", guardada.getId(), mascota.getId());
        return mapToDTO(guardada);
    }

    public CitaResponseDTO actualizar(Long id, CitaRequestDTO dto) {
        Cita existente = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cita no encontrada con id: " + id));
        // Revalidamos la mascota por si cambio
        MascotaExternaDTO mascota = usuariosClient.obtenerMascota(dto.getMascotaId());

        existente.setMascotaId(mascota.getId());
        existente.setMascotaNombre(mascota.getNombre());
        existente.setMotivo(dto.getMotivo());
        existente.setFecha(dto.getFecha());
        existente.setHora(dto.getHora());
        existente.setProductoId(dto.getProductoId());
        existente.setCantidadProducto(dto.getCantidadProducto());
        log.info("Cita actualizada con id: {}", id);
        return mapToDTO(citaRepository.save(existente));
    }

    // Cambia el estado de la cita (regla de negocio simple)
    public CitaResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cita no encontrada con id: " + id));
        cita.setEstado(nuevoEstado);
        log.info("Cita {} cambio a estado: {}", id, nuevoEstado);
        return mapToDTO(citaRepository.save(cita));
    }

    public void eliminar(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Cita no encontrada con id: " + id);
        }
        citaRepository.deleteById(id);
        log.info("Cita eliminada con id: {}", id);
    }
}
