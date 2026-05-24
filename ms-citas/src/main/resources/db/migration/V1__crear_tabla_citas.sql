-- ═══════════════════════════════════════════════════
-- V1__crear_tabla_citas.sql
--
-- NOTA DE DISENO: esta tabla NO tiene FK hacia mascotas
-- ni productos, porque esas entidades viven en OTRAS
-- bases de datos (otros microservicios). Solo guardamos
-- los IDs de referencia. La existencia se valida por
-- WebClient en tiempo de ejecucion. Esto respeta la
-- autonomia de datos de cada microservicio.
-- ═══════════════════════════════════════════════════

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
