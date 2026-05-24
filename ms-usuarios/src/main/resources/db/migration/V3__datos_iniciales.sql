-- ═══════════════════════════════════════════════════
-- V3__datos_iniciales.sql
-- Datos de prueba. Flyway los inserta una sola vez.
-- ═══════════════════════════════════════════════════

INSERT INTO duenos (nombre, rut, email, telefono) VALUES
    ('Juan Perez',     '11111111-1', 'juan.perez@mail.com',   '+56911111111'),
    ('Maria Gonzalez', '22222222-2', 'maria.gonzalez@mail.com','+56922222222'),
    ('Carlos Soto',    '33333333-3', 'carlos.soto@mail.com',  '+56933333333');

INSERT INTO mascotas (nombre, especie, raza, edad, dueno_id) VALUES
    ('Firulais', 'Perro', 'Labrador',       3, 1),
    ('Michi',    'Gato',  'Siames',         2, 1),
    ('Rocky',    'Perro', 'Pastor Aleman',  5, 2),
    ('Pelusa',   'Conejo', NULL,            1, 3);
