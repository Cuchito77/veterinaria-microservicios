package com.veterinaria.login.repository;

import com.veterinaria.login.model.UsuarioCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioCuentaRepository extends JpaRepository<UsuarioCuenta, Long> {
    // Query Method: busca una cuenta por su username (clave del login)
    Optional<UsuarioCuenta> findByUsername(String username);
}
