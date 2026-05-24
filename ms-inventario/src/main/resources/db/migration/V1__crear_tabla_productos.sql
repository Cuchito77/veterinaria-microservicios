-- ═══════════════════════════════════════════════════
-- V1__crear_tabla_productos.sql
-- Tabla de productos del inventario de la veterinaria.
-- ═══════════════════════════════════════════════════

CREATE TABLE productos (
    id        BIGINT        NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(100)  NOT NULL,
    categoria VARCHAR(50)   NOT NULL,
    precio    DECIMAL(10,2) NOT NULL,
    stock     INT           NOT NULL,
    PRIMARY KEY (id)
);
