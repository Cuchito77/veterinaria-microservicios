package com.veterinaria.citas.config;

import com.veterinaria.citas.model.Cita;
import com.veterinaria.citas.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

// ═══════════════════════════════════════════════════
// Poblamiento automatico de la tabla "citas" con
// DataFaker. Se ejecuta al arrancar la aplicacion y es
// IDEMPOTENTE: si la tabla ya tiene datos suficientes
// (10 o mas registros), no inserta nada y se omite.
//
// IMPORTANTE (autonomia de datos): esta tabla NO tiene
// FK hacia mascotas ni productos, porque viven en OTRAS
// bases de datos (otros microservicios). Por eso aqui
// generamos mascota_id aleatorio entre 1 y 30 (rango que
// puebla ms-usuarios) y producto_id entre 1 y 15 (rango
// de ms-inventario): son referencias logicas, no FKs.
// ═══════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private static final int UMBRAL_DATOS = 10;
    private static final int CANTIDAD_CITAS = 25;

    // Motivos realistas de consulta veterinaria
    private static final String[] MOTIVOS = {
            "Control sano",
            "Vacunacion anual",
            "Desparasitacion",
            "Herida en pata",
            "Control post operatorio",
            "Limpieza dental",
            "Problemas digestivos",
            "Corte de unas",
            "Revision de piel y pelaje",
            "Control de peso y nutricion"
    };

    // Estados validos segun los seeds de Flyway (V2__datos_iniciales.sql)
    private static final String[] ESTADOS = {"PROGRAMADA", "ATENDIDA"};

    private final CitaRepository citaRepository;

    @Override
    public void run(String... args) {
        // Idempotencia: si la tabla ya tiene datos, no se vuelve a poblar
        if (citaRepository.count() >= UMBRAL_DATOS) {
            log.info("DataLoader: ya hay datos, se omite el poblamiento");
            return;
        }

        Faker faker = new Faker(new Locale("es"));
        Random random = new Random();

        List<Cita> citas = new ArrayList<>();
        for (int i = 0; i < CANTIDAD_CITAS; i++) {
            Cita cita = new Cita();

            // Referencia logica a ms-usuarios: ids 1 a 30 (rango que puebla ese servicio).
            // Guardamos tambien un nombre de mascota generado (proyeccion local).
            cita.setMascotaId((long) (random.nextInt(30) + 1));
            cita.setMascotaNombre(faker.dog().name());

            cita.setMotivo(MOTIVOS[random.nextInt(MOTIVOS.length)]);

            // Fecha: entre hoy y +60 dias
            cita.setFecha(LocalDate.now().plusDays(random.nextInt(61)));

            // Hora: bloques de 30 minutos entre 09:00 y 18:00
            cita.setHora(LocalTime.of(9, 0).plusMinutes(30L * random.nextInt(19)));

            // Producto: ~la mitad de las citas no consume producto (null);
            // el resto referencia un producto_id 1 a 15 de ms-inventario
            // con una cantidad pequena (1 a 3).
            if (random.nextBoolean()) {
                cita.setProductoId((long) (random.nextInt(15) + 1));
                cita.setCantidadProducto(random.nextInt(3) + 1);
            } else {
                cita.setProductoId(null);
                cita.setCantidadProducto(null);
            }

            // Estado: distribucion variada entre los estados de los seeds
            cita.setEstado(ESTADOS[random.nextInt(ESTADOS.length)]);

            citas.add(cita);
        }

        citaRepository.saveAll(citas);
        log.info("DataLoader: se insertaron {} citas falsas con DataFaker", citas.size());
    }
}
