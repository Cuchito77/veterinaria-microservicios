-- ═══════════════════════════════════════════════════
-- V1__crear_tabla_duenos.sql
-- Tabla de duenos (clientes de la veterinaria).
-- Flyway ejecuta este script primero (version 1).
-- ═══════════════════════════════════════════════════

CREATE TABLE duenos (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(100) NOT NULL,
    rut       VARCHAR(12)  NOT NULL,
    email     VARCHAR(100) NOT NULL,
    telefono  VARCHAR(20)  NULL,
    PRIMARY KEY (id),
    -- El RUT no se puede repetir (regla de negocio a nivel BD)
    CONSTRAINT uk_dueno_rut UNIQUE (rut)
);
