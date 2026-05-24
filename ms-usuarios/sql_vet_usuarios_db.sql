-- ═══════════════════════════════════════════════════
-- BASE DE DATOS: vet_usuarios_db  (MS-USUARIOS)
-- Dueños y mascotas. Si usas Flyway NO ejecutes esto.
-- ═══════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS vet_usuarios_db;
USE vet_usuarios_db;

CREATE TABLE duenos (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(100) NOT NULL,
    rut       VARCHAR(12)  NOT NULL,
    email     VARCHAR(100) NOT NULL,
    telefono  VARCHAR(20)  NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_dueno_rut UNIQUE (rut)
);

CREATE TABLE mascotas (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(80)  NOT NULL,
    especie   VARCHAR(50)  NOT NULL,
    raza      VARCHAR(60)  NULL,
    edad      INT          NOT NULL,
    dueno_id  BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_mascota_dueno
        FOREIGN KEY (dueno_id) REFERENCES duenos(id)
);

INSERT INTO duenos (nombre, rut, email, telefono) VALUES
    ('Juan Perez',     '11111111-1', 'juan.perez@mail.com',    '+56911111111'),
    ('Maria Gonzalez', '22222222-2', 'maria.gonzalez@mail.com','+56922222222'),
    ('Carlos Soto',    '33333333-3', 'carlos.soto@mail.com',   '+56933333333');

INSERT INTO mascotas (nombre, especie, raza, edad, dueno_id) VALUES
    ('Firulais', 'Perro',  'Labrador',      3, 1),
    ('Michi',    'Gato',   'Siames',        2, 1),
    ('Rocky',    'Perro',  'Pastor Aleman', 5, 2),
    ('Pelusa',   'Conejo', NULL,            1, 3);
