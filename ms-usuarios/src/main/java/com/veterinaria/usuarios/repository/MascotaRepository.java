package com.veterinaria.usuarios.repository;

import com.veterinaria.usuarios.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    // Query Method: todas las mascotas de un dueno especifico
    List<Mascota> findByDuenoId(Long duenoId);
}
