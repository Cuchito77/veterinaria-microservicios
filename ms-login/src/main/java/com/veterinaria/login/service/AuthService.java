package com.veterinaria.login.service;

import com.veterinaria.login.dto.LoginRequestDTO;
import com.veterinaria.login.dto.LoginResponseDTO;
import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import com.veterinaria.login.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

// ═══════════════════════════════════════════════════
// AuthService: logica de autenticacion.
// 1) Valida username + password (BCrypt) contra la BD.
// 2) Si las credenciales son validas, GENERA un JWT.
// ═══════════════════════════════════════════════════

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioCuentaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        log.info("Intento de login para usuario: {}", dto.getUsername());

        Optional<UsuarioCuenta> cuentaOpt =
                usuarioRepository.findByUsername(dto.getUsername());

        // Caso 1: el usuario no existe
        if (cuentaOpt.isEmpty()) {
            log.warn("Login fallido: usuario {} no existe", dto.getUsername());
            return new LoginResponseDTO(false,
                    "Usuario o contrasena incorrectos", dto.getUsername(), null, null);
        }

        UsuarioCuenta cuenta = cuentaOpt.get();

        // Caso 2: la cuenta esta inactiva
        if (!cuenta.getActivo()) {
            log.warn("Login fallido: cuenta {} inactiva", dto.getUsername());
            return new LoginResponseDTO(false,
                    "La cuenta esta inactiva", dto.getUsername(), null, null);
        }

        // Caso 3: la password no coincide (se compara contra el hash BCrypt)
        if (!passwordEncoder.matches(dto.getPassword(), cuenta.getPassword())) {
            log.warn("Login fallido: password incorrecta para {}", dto.getUsername());
            return new LoginResponseDTO(false,
                    "Usuario o contrasena incorrectos", dto.getUsername(), null, null);
        }

        // Caso 4: autenticacion exitosa -> generamos el JWT
        String token = jwtService.generarToken(cuenta.getUsername(), cuenta.getRol());
        log.info("Login exitoso para usuario: {} (rol: {})",
                cuenta.getUsername(), cuenta.getRol());
        return new LoginResponseDTO(true,
                "Autenticacion exitosa", cuenta.getUsername(), cuenta.getRol(), token);
    }
}
