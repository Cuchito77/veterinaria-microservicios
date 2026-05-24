-- ═══════════════════════════════════════════════════
-- V2__crear_tabla_mascotas.sql
-- Se ejecuta despues de V1. Puede referenciar "duenos"
-- porque V1 ya creo esa tabla.
-- ═══════════════════════════════════════════════════

CREATE TABLE mascotas (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(80)  NOT NULL,
    especie   VARCHAR(50)  NOT NULL,
    raza      VARCHAR(60)  NULL,
    edad      INT          NOT NULL,
    dueno_id  BIGINT       NOT NULL,
    PRIMARY KEY (id),
    -- FK hacia duenos: garantiza integridad referencial
    CONSTRAINT fk_mascota_dueno
        FOREIGN KEY (dueno_id) REFERENCES duenos(id)
);
