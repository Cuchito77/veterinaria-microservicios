package com.veterinaria.login.config;

import com.veterinaria.login.model.UsuarioCuenta;
import com.veterinaria.login.repository.UsuarioCuentaRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

// ═══════════════════════════════════════════════════
// Poblamiento automatico de la tabla "usuarios" con
// DataFaker. Genera cuentas falsas con username unico,
// password cifrada con BCrypt, rol aleatorio y estado
// activo en ~80% de los casos. Es idempotente: si la
// tabla ya tiene suficientes registros, no hace nada.
// Independiente de PasswordMigrationRunner (las
// passwords ya se insertan cifradas).
// ═══════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private static final int CANTIDAD_CUENTAS = 10;
    private static final String[] ROLES = {"ADMIN", "VETERINARIO", "RECEPCION"};

    private final UsuarioCuentaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Idempotencia: si la tabla ya esta poblada, se omite
        if (usuarioRepository.count() >= CANTIDAD_CUENTAS) {
            log.info("DataLoader: ya hay datos, se omite el poblamiento");
            return;
        }

        Faker faker = new Faker(new Locale("es"));
        Random random = new Random();
        Set<String> usadosEnEstaCorrida = new HashSet<>();
        int insertadas = 0;

        while (insertadas < CANTIDAD_CUENTAS) {
            // Normaliza el username: minusculas, sin espacios ni caracteres raros
            String username = normalizar(faker.internet().username());

            // Unicidad: contra los generados en esta corrida y contra la BD
            // (la columna username tiene constraint UNIQUE en V1)
            if (username.isEmpty()
                    || usadosEnEstaCorrida.contains(username)
                    || usuarioRepository.findByUsername(username).isPresent()) {
                continue;
            }
            usadosEnEstaCorrida.add(username);

            UsuarioCuenta cuenta = new UsuarioCuenta();
            cuenta.setUsername(username);
            // Las passwords SIEMPRE se guardan cifradas con BCrypt
            cuenta.setPassword(passwordEncoder.encode(faker.internet().password(8, 12)));
            cuenta.setRol(ROLES[random.nextInt(ROLES.length)]);
            // Activo en ~80% de los casos
            cuenta.setActivo(random.nextInt(100) < 80);

            usuarioRepository.save(cuenta);
            insertadas++;
        }

        log.info("DataLoader: cuentas falsas insertadas: {}", insertadas);
    }

    // Limpia el username generado por Faker: minusculas y solo
    // letras, numeros, punto, guion y guion bajo (max 50 chars)
    private String normalizar(String username) {
        String limpio = username.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "");
        return limpio.length() > 50 ? limpio.substring(0, 50) : limpio;
    }
}
