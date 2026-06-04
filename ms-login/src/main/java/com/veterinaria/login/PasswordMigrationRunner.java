package com.veterinaria.login;

import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// ═══════════════════════════════════════════════════
// Helper de migracion (una sola vez).
// Los datos de prueba (V2__datos_iniciales.sql) se
// insertaron con passwords en TEXTO PLANO. Al arrancar,
// este runner detecta esas passwords (no empiezan con
// "$2") y las recifra con BCrypt. Es idempotente: en los
// siguientes arranques ya estan cifradas y se omiten.
// ═══════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
public class PasswordMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final UsuarioCuentaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        int migradas = 0;
        for (UsuarioCuenta cuenta : usuarioRepository.findAll()) {
            String pass = cuenta.getPassword();
            // Un hash BCrypt siempre empieza por "$2"; si no, esta en texto plano
            if (pass != null && !pass.startsWith("$2")) {
                cuenta.setPassword(passwordEncoder.encode(pass));
                usuarioRepository.save(cuenta);
                migradas++;
            }
        }
        if (migradas > 0) {
            log.info("Passwords migradas a BCrypt: {}", migradas);
        }
    }
}
