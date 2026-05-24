package com.veterinaria.citas.repository;

import com.veterinaria.citas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    // Query Method: todas las citas de una mascota
    List<Cita> findByMascotaId(Long mascotaId);

    // Query Method: citas por estado (PROGRAMADA, ATENDIDA, CANCELADA)
    List<Cita> findByEstado(String estado);
}
