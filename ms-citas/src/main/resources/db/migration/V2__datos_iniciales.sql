-- ═══════════════════════════════════════════════════
-- V2__datos_iniciales.sql
-- Citas de prueba. Los mascota_id (1,2,3) coinciden con
-- las mascotas insertadas en ms-usuarios.
-- ═══════════════════════════════════════════════════

INSERT INTO citas
    (mascota_id, mascota_nombre, motivo, fecha, hora, producto_id, cantidad_producto, estado)
VALUES
    (1, 'Firulais', 'Vacunacion anual',      '2025-07-01', '09:00:00', 1, 1, 'PROGRAMADA'),
    (2, 'Michi',    'Control de rutina',     '2025-07-01', '10:30:00', NULL, NULL, 'PROGRAMADA'),
    (3, 'Rocky',    'Desparasitacion',       '2025-07-02', '11:00:00', 4, 1, 'ATENDIDA');
