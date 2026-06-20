package com.veterinaria.usuarios.config;

import com.veterinaria.usuarios.model.Dueno;
import com.veterinaria.usuarios.model.Mascota;
import com.veterinaria.usuarios.repository.DuenoRepository;
import com.veterinaria.usuarios.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

// ═══════════════════════════════════════════════════
// Poblamiento automatico de tablas con DataFaker.
// Al arrancar genera 20 duenos y 30 mascotas con datos
// falsos pero realistas (RUT chileno valido incluido).
// Es idempotente: si la BD ya tiene 10 o mas duenos
// (los seeds de Flyway insertan 3) se omite, asi solo
// puebla la primera vez que se levanta el servicio.
// ═══════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final DuenoRepository duenoRepository;
    private final MascotaRepository mascotaRepository;

    // Especies posibles y razas fijas para las que DataFaker no cubre
    private static final String[] ESPECIES = {"Perro", "Gato", "Conejo", "Ave", "Hamster"};
    private static final String[] RAZAS_GATO = {"Siames", "Persa", "Angora", "Bengala", "Comun Europeo"};
    private static final String[] RAZAS_CONEJO = {"Enano Holandes", "Cabeza de Leon", "Rex", "Belier"};
    private static final String[] RAZAS_AVE = {"Canario", "Periquito", "Cacatua", "Agapornis"};
    private static final String[] RAZAS_HAMSTER = {"Sirio", "Ruso", "Roborovski", "Chino"};

    @Override
    public void run(String... args) {
        // IDEMPOTENTE: si ya se poblo antes (o hay datos suficientes), no hacemos nada
        if (duenoRepository.count() >= 10) {
            log.info("DataLoader: ya hay datos, se omite el poblamiento");
            return;
        }

        Faker faker = new Faker(new Locale("es"));
        Random random = new Random();

        // ── 1. Generar 20 duenos ──────────────────────
        // Para garantizar RUTs unicos partimos de un numero base
        // aleatorio entre 5.000.000 y 25.000.000 y vamos incrementando
        int rutBase = 5_000_000 + random.nextInt(20_000_000 - 20);

        List<Dueno> duenos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String nombre = faker.name().fullName();
            if (nombre.length() > 100) {
                nombre = nombre.substring(0, 100);
            }
            int numeroRut = rutBase + i;
            String rut = numeroRut + "-" + calcularDigitoVerificador(numeroRut);
            String email = faker.internet().emailAddress();
            // Celular chileno: +569 seguido de 8 digitos (12 chars, cabe en 20)
            String telefono = "+569" + String.format("%08d", random.nextInt(100_000_000));

            // Constructor de Lombok: (id, nombre, rut, email, telefono)
            duenos.add(new Dueno(null, nombre, rut, email, telefono));
        }
        List<Dueno> duenosGuardados = duenoRepository.saveAll(duenos);

        // ── 2. Generar 30 mascotas ────────────────────
        List<Mascota> mascotas = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String especie = ESPECIES[random.nextInt(ESPECIES.length)];
            String nombre = faker.dog().name();
            if (nombre.length() > 80) {
                nombre = nombre.substring(0, 80);
            }
            String raza = razaSegunEspecie(especie, faker, random);
            int edad = 1 + random.nextInt(15); // entre 1 y 15
            Dueno dueno = duenosGuardados.get(random.nextInt(duenosGuardados.size()));

            // Constructor de Lombok: (id, nombre, especie, raza, edad, dueno)
            mascotas.add(new Mascota(null, nombre, especie, raza, edad, dueno));
        }
        mascotaRepository.saveAll(mascotas);

        log.info("DataLoader: poblamiento completado -> {} duenos y {} mascotas insertados",
                duenosGuardados.size(), mascotas.size());
    }

    // ═══════════════════════════════════════════════════
    // Calcula el digito verificador (DV) de un RUT chileno
    // con el algoritmo de modulo 11:
    //   1. Se recorren los digitos del numero de derecha a
    //      izquierda multiplicando cada uno por la serie
    //      ciclica 2, 3, 4, 5, 6, 7 y sumando los productos.
    //   2. Se calcula el resto: resto = suma % 11
    //   3. DV = 11 - resto. Casos especiales:
    //      - si da 11 el DV es "0"
    //      - si da 10 el DV es "K"
    //      - en otro caso, el DV es el numero obtenido
    // ═══════════════════════════════════════════════════
    private String calcularDigitoVerificador(int numeroRut) {
        int suma = 0;
        int multiplicador = 2;
        int numero = numeroRut;
        while (numero > 0) {
            suma += (numero % 10) * multiplicador;
            numero /= 10;
            multiplicador++;
            if (multiplicador > 7) {
                multiplicador = 2; // la serie vuelve a empezar en 2
            }
        }
        int dv = 11 - (suma % 11);
        if (dv == 11) {
            return "0";
        }
        if (dv == 10) {
            return "K";
        }
        return String.valueOf(dv);
    }

    // Devuelve una raza coherente con la especie:
    // para Perro usamos DataFaker; para el resto, listas fijas
    private String razaSegunEspecie(String especie, Faker faker, Random random) {
        return switch (especie) {
            case "Perro" -> truncarRaza(faker.dog().breed());
            case "Gato" -> RAZAS_GATO[random.nextInt(RAZAS_GATO.length)];
            case "Conejo" -> RAZAS_CONEJO[random.nextInt(RAZAS_CONEJO.length)];
            case "Ave" -> RAZAS_AVE[random.nextInt(RAZAS_AVE.length)];
            case "Hamster" -> RAZAS_HAMSTER[random.nextInt(RAZAS_HAMSTER.length)];
            default -> null;
        };
    }

    // La columna raza admite maximo 60 caracteres
    private String truncarRaza(String raza) {
        if (raza != null && raza.length() > 60) {
            return raza.substring(0, 60);
        }
        return raza;
    }
}
