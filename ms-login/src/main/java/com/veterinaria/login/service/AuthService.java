package com.veterinaria.login.service;

import com.veterinaria.login.dto.LoginRequestDTO;
import com.veterinaria.login.dto.LoginResponseDTO;
import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Optional;

// ═══════════════════════════════════════════════════
// AuthService: logica de autenticacion SIMPLE.
// Compara username + password contra la BD.
// No genera token; solo confirma si las credenciales
// son validas y devuelve el rol del usuario.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioCuentaRepository usuarioRepository;

    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        log.info("Intento de login para usuario: {}", dto.getUsername());

        Optional<UsuarioCuenta> cuentaOpt =
                usuarioRepository.findByUsername(dto.getUsername());

        // Caso 1: el usuario no existe
        if (cuentaOpt.isEmpty()) {
            log.warn("Login fallido: usuario {} no existe", dto.getUsername());
            return new LoginResponseDTO(false,
                    "Usuario o contrasena incorrectos", dto.getUsername(), null);
        }

        UsuarioCuenta cuenta = cuentaOpt.get();

        // Caso 2: la cuenta esta inactiva
        if (!cuenta.getActivo()) {
            log.warn("Login fallido: cuenta {} inactiva", dto.getUsername());
            return new LoginResponseDTO(false,
                    "La cuenta esta inactiva", dto.getUsername(), null);
        }

        // Caso 3: la password no coincide
        if (!cuenta.getPassword().equals(dto.getPassword())) {
            log.warn("Login fallido: password incorrecta para {}", dto.getUsername());
            return new LoginResponseDTO(false,
                    "Usuario o contrasena incorrectos", dto.getUsername(), null);
        }

        // Caso 4: autenticacion exitosa
        log.info("Login exitoso para usuario: {} (rol: {})",
                cuenta.getUsername(), cuenta.getRol());
        return new LoginResponseDTO(true,
                "Autenticacion exitosa", cuenta.getUsername(), cuenta.getRol());
    }
}
