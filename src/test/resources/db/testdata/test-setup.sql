-- Test setup data for DataJpaTest (uses Hibernate create-drop, not Flyway)
INSERT INTO medicos (id, nombre_completo, especialidad) VALUES
    ('a1b2c3d4-0001-0001-0001-000000000001', 'Dra. Ana Garcia Lopez',    'Cardiologia'),
    ('a1b2c3d4-0002-0002-0002-000000000002', 'Dr. Carlos Ramos Herrera', 'Medicina General');

INSERT INTO pacientes (id, nombre_completo, numero_whatsapp) VALUES
    ('b1c2d3e4-0001-0001-0001-000000000001', 'Juan Perez Ortiz',   '+593987654321'),
    ('b1c2d3e4-0002-0002-0002-000000000002', 'Maria Lopez Torres', '+593912345678');

INSERT INTO franjas_horarias (id, medico_id, fecha, hora_inicio, hora_fin, estado) VALUES
    ('c1d2e3f4-0001-0001-0001-000000000001',
     'a1b2c3d4-0001-0001-0001-000000000001',
     DATEADD('DAY', 7, CURRENT_DATE), '09:00:00', '09:30:00', 'DISPONIBLE'),
    ('c1d2e3f4-0002-0002-0002-000000000002',
     'a1b2c3d4-0001-0001-0001-000000000001',
     DATEADD('DAY', 7, CURRENT_DATE), '09:30:00', '10:00:00', 'DISPONIBLE'),
    ('c1d2e3f4-0003-0003-0003-000000000003',
     'a1b2c3d4-0002-0002-0002-000000000002',
     DATEADD('DAY', 7, CURRENT_DATE), '10:00:00', '10:30:00', 'DISPONIBLE'),
    ('c1d2e3f4-0004-0004-0004-000000000004',
     'a1b2c3d4-0001-0001-0001-000000000001',
     DATEADD('DAY', 7, CURRENT_DATE), '10:00:00', '10:30:00', 'OCUPADA');
