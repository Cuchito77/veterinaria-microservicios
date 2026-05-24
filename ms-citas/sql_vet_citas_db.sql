-- ═══════════════════════════════════════════════════
-- BASE DE DATOS: vet_citas_db  (MS-CITAS)
-- Sin FK hacia mascotas/productos (viven en otras BD).
-- Si usas Flyway NO ejecutes esto.
-- ═══════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS vet_citas_db;
USE vet_citas_db;

CREATE TABLE citas (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    mascota_id        BIGINT       NOT NULL,
    mascota_nombre    VARCHAR(80)  NULL,
    motivo            VARCHAR(200) NOT NULL,
    fecha             DATE         NOT NULL,
    hora              TIME         NOT NULL,
    producto_id       BIGINT       NULL,
    cantidad_producto INT          NULL,
    estado            VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO citas
    (mascota_id, mascota_nombre, motivo, fecha, hora, producto_id, cantidad_producto, estado)
VALUES
    (1, 'Firulais', 'Vacunacion anual',  '2025-07-01', '09:00:00', 1, 1, 'PROGRAMADA'),
    (2, 'Michi',    'Control de rutina', '2025-07-01', '10:30:00', NULL, NULL, 'PROGRAMADA'),
    (3, 'Rocky',    'Desparasitacion',   '2025-07-02', '11:00:00', 4, 1, 'ATENDIDA');
