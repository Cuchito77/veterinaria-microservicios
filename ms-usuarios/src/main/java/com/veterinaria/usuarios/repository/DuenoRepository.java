package com.veterinaria.usuarios.repository;

import com.veterinaria.usuarios.model.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DuenoRepository extends JpaRepository<Dueno, Long> {
    // Query Method: busca un dueno por su RUT (para evitar duplicados)
    Optional<Dueno> findByRut(String rut);
}
