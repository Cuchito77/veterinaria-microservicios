-- ═════════════════════════════════════════════════════
-- INIT-DB · Se ejecuta UNA vez al crear el contenedor MySQL
-- (docker-entrypoint-initdb.d). Crea las 4 bases vacias;
-- Flyway crea las tablas cuando arranca cada microservicio.
-- ═════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS vet_login_db;
CREATE DATABASE IF NOT EXISTS vet_usuarios_db;
CREATE DATABASE IF NOT EXISTS vet_citas_db;
CREATE DATABASE IF NOT EXISTS vet_inventario_db;
