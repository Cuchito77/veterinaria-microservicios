-- ═══════════════════════════════════════════════════
-- BASE DE DATOS: vet_login_db  (MS-LOGIN)
-- Pega esto en Laragon (HeidiSQL) si quieres crear las
-- tablas manualmente. OJO: si usas Flyway NO ejecutes
-- esto, porque Flyway las crea solo y daria conflicto.
-- ═══════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS vet_login_db;
USE vet_login_db;

CREATE TABLE usuarios (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    username  VARCHAR(50)  NOT NULL,
    password  VARCHAR(100) NOT NULL,
    rol       VARCHAR(30)  NOT NULL,
    activo    BIT(1)       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_username UNIQUE (username)
);

INSERT INTO usuarios (username, password, rol, activo) VALUES
    ('admin',    'admin123',  'ADMIN',       b'1'),
    ('vet',      'vet123',    'VETERINARIO', b'1'),
    ('recepcion','recep123',  'RECEPCION',   b'1'),
    ('inactivo', 'test123',   'RECEPCION',   b'0');
