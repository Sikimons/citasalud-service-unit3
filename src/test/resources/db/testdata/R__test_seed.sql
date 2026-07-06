-- Repeatable seed: truncate and re-insert before each test run
DELETE FROM citas;
DELETE FROM franjas_horarias;
DELETE FROM pacientes;

INSERT INTO pacientes (id, nombre_completo, numero_whatsapp) VALUES
    ('b1c2d3e4-0001-0001-0001-000000000001', 'Juan Perez Ortiz',   '+593987654321'),
    ('b1c2d3e4-0002-0002-0002-000000000002', 'Maria Lopez Torres', '+593912345678');

-- Future-dated franjas so tests don't fail on past-date validation
INSERT INTO franjas_horarias (id, medico_id, fecha, hora_inicio, hora_fin, estado) VALUES
    ('c1d2e3f4-0001-0001-0001-000000000001',
     'a1b2c3d4-0001-0001-0001-000000000001',
     CURRENT_DATE + INTERVAL '7 days', '09:00', '09:30', 'DISPONIBLE'),
    ('c1d2e3f4-0002-0002-0002-000000000002',
     'a1b2c3d4-0001-0001-0001-000000000001',
     CURRENT_DATE + INTERVAL '7 days', '09:30', '10:00', 'DISPONIBLE'),
    ('c1d2e3f4-0003-0003-0003-000000000003',
     'a1b2c3d4-0002-0002-0002-000000000002',
     CURRENT_DATE + INTERVAL '7 days', '10:00', '10:30', 'DISPONIBLE'),
    ('c1d2e3f4-0004-0004-0004-000000000004',
     'a1b2c3d4-0001-0001-0001-000000000001',
     CURRENT_DATE + INTERVAL '7 days', '10:00', '10:30', 'OCUPADA');
