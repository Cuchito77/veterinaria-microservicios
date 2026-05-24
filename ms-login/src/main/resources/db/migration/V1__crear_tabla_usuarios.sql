-- ═══════════════════════════════════════════════════
-- V1__crear_tabla_usuarios.sql
-- Tabla de cuentas de acceso al sistema.
-- ═══════════════════════════════════════════════════

CREATE TABLE usuarios (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    username  VARCHAR(50)  NOT NULL,
    password  VARCHAR(100) NOT NULL,
    rol       VARCHAR(30)  NOT NULL,
    activo    BIT(1)       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_username UNIQUE (username)
);
