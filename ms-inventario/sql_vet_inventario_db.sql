-- ═══════════════════════════════════════════════════
-- BASE DE DATOS: vet_inventario_db  (MS-INVENTARIO)
-- Productos. Si usas Flyway NO ejecutes esto.
-- ═══════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS vet_inventario_db;
USE vet_inventario_db;

CREATE TABLE productos (
    id        BIGINT        NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(100)  NOT NULL,
    categoria VARCHAR(50)   NOT NULL,
    precio    DECIMAL(10,2) NOT NULL,
    stock     INT           NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO productos (nombre, categoria, precio, stock) VALUES
    ('Vacuna Antirrabica',   'Vacuna',      8500.00,  50),
    ('Alimento Perro 15kg',  'Alimento',    25990.00, 30),
    ('Alimento Gato 3kg',    'Alimento',    9990.00,  40),
    ('Antiparasitario Oral', 'Medicamento', 4500.00,  100),
    ('Collar Antipulgas',    'Accesorio',   6990.00,  25);
