package com.veterinaria.inventario.config;

import com.veterinaria.inventario.model.Producto;
import com.veterinaria.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// ═══════════════════════════════════════════════════
// Poblamiento automatico de la tabla "productos" con
// DataFaker. Al arrancar genera 15 productos veterinarios
// realistas (nombre, categoria, precio CLP y stock).
// Es idempotente: si la tabla ya tiene 10 o mas registros
// (los seeds de Flyway + datos previos) no inserta nada.
// ═══════════════════════════════════════════════════

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private static final int CANTIDAD_PRODUCTOS = 15;

    private final ProductoRepository productoRepository;

    // Categorias usadas en los seeds de Flyway (V2__datos_iniciales.sql)
    private static final String[] CATEGORIAS = {"Vacuna", "Alimento", "Medicamento", "Accesorio"};

    @Override
    public void run(String... args) {
        // Idempotente: si ya hay suficientes registros se omite
        if (productoRepository.count() >= 10) {
            log.info("ya hay datos, se omite el poblamiento");
            return;
        }

        Faker faker = new Faker(new Locale("es"));
        List<Producto> productos = new ArrayList<>();

        for (int i = 0; i < CANTIDAD_PRODUCTOS; i++) {
            String categoria = CATEGORIAS[i % CATEGORIAS.length];
            Producto producto = new Producto();
            producto.setNombre(generarNombre(faker, categoria));
            producto.setCategoria(categoria);
            // Precio CLP sin decimales, entre 1.990 y 89.990
            producto.setPrecio(BigDecimal.valueOf(faker.number().numberBetween(1990L, 89991L)));
            // Stock entre 5 y 100
            producto.setStock(faker.number().numberBetween(5, 101));
            productos.add(producto);
        }

        productoRepository.saveAll(productos);
        log.info("Poblamiento con DataFaker completado: {} productos insertados", productos.size());
    }

    // Combina un prefijo segun la categoria con datos generados
    // por Faker para obtener nombres realistas, ej:
    // "Alimento Premium Perro 15kg", "Vacuna Triple Felina Lab Rivera"
    private String generarNombre(Faker faker, String categoria) {
        return switch (categoria) {
            case "Vacuna" -> "Vacuna " + faker.options().option("Antirrabica", "Triple Felina", "Sextuple", "Parvovirus", "Leucemia Felina")
                    + " " + faker.options().option("Canina", "Felina", "Cachorro", "Adulto");
            case "Alimento" -> "Alimento " + faker.options().option("Premium", "Light", "Senior", "Cachorro", "Grain Free")
                    + " " + faker.options().option("Perro", "Gato", "Conejo", "Ave")
                    + " " + faker.number().numberBetween(1, 20) + "kg";
            case "Medicamento" -> faker.options().option("Antiparasitario", "Antibiotico", "Antiinflamatorio", "Analgesico", "Vitaminas")
                    + " " + faker.options().option("Oral", "Inyectable", "Topico", "Masticable")
                    + " " + faker.number().numberBetween(50, 500) + "mg";
            default -> faker.options().option("Collar", "Correa", "Arnes", "Juguete", "Cama")
                    + " " + faker.options().option("Antipulgas", "Reflectante", "Acolchado", "Resistente", "Ajustable")
                    + " " + faker.color().name();
        };
    }
}
