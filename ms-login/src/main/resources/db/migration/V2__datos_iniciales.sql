-- ═══════════════════════════════════════════════════
-- V2__datos_iniciales.sql
-- Cuentas de prueba con distintos roles.
-- (Passwords en texto plano: autenticacion simple.)
-- ═══════════════════════════════════════════════════

INSERT INTO usuarios (username, password, rol, activo) VALUES
    ('admin',    'admin123',  'ADMIN',       b'1'),
    ('vet',      'vet123',    'VETERINARIO', b'1'),
    ('recepcion','recep123',  'RECEPCION',   b'1'),
    ('inactivo', 'test123',   'RECEPCION',   b'0');
