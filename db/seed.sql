-- =========================================================
-- Datos de prueba (dummies) para el Parking System
-- Idempotente: se puede ejecutar las veces que quieras
-- (no duplica ni borra datos existentes).
-- =========================================================

-- ── Vehículos de prueba (idempotente por placa) ──────────
INSERT INTO vehiculos (placa, tipo) VALUES
  ('OFI001', 'OFICIAL'),
  ('OFI002', 'OFICIAL'),
  ('RES001', 'RESIDENTE'),
  ('RES002', 'RESIDENTE'),
  ('NR001',  'NO_RESIDENTE'),
  ('NR002',  'NO_RESIDENTE'),
  ('NR003',  'NO_RESIDENTE')
ON CONFLICT (placa) DO NOTHING;

-- ── Residentes con tiempo acumulado (idempotente) ─────────
-- 1520 min ≈ 25.3 h → monto reporte = 1520 × $0.05 = $76.00
--  480 min ≈  8.0 h → monto reporte =  480 × $0.05 = $24.00
INSERT INTO residentes (placa, tiempo_acumulado_minutos, ultimo_reinicio) VALUES
  ('RES001', 1520, NOW()),
  ('RES002',  480, NOW())
ON CONFLICT (placa) DO NOTHING;

-- ── Estancias finalizadas · NO RESIDENTE ($0.50/min) ──────
-- NR001: 45 min → $22.50
INSERT INTO estancias (vehiculo_placa, fecha_entrada, fecha_salida, costo_total)
SELECT 'NR001', '2026-09-08 09:00:00', '2026-09-08 09:45:00', 22.50
WHERE NOT EXISTS (
  SELECT 1 FROM estancias
  WHERE vehiculo_placa = 'NR001' AND fecha_entrada = '2026-09-08 09:00:00'
);

-- NR002: 90 min → $45.00
INSERT INTO estancias (vehiculo_placa, fecha_entrada, fecha_salida, costo_total)
SELECT 'NR002', '2026-09-08 10:00:00', '2026-09-08 11:30:00', 45.00
WHERE NOT EXISTS (
  SELECT 1 FROM estancias
  WHERE vehiculo_placa = 'NR002' AND fecha_entrada = '2026-09-08 10:00:00'
);

-- ── Estancias finalizadas · OFICIAL (costo $0) ────────────
-- OFI001: jornada de 9 h, costo 0
INSERT INTO estancias (vehiculo_placa, fecha_entrada, fecha_salida, costo_total)
SELECT 'OFI001', '2026-09-08 08:00:00', '2026-09-08 17:00:00', 0
WHERE NOT EXISTS (
  SELECT 1 FROM estancias
  WHERE vehiculo_placa = 'OFI001' AND fecha_entrada = '2026-09-08 08:00:00'
);

-- ── Estancias finalizadas · RESIDENTE (acumula, no cobra) ─
-- RES001: 30 min → costo 0 (el tiempo se acumula en residentes)
INSERT INTO estancias (vehiculo_placa, fecha_entrada, fecha_salida, costo_total)
SELECT 'RES001', '2026-09-08 09:00:00', '2026-09-08 09:30:00', 0
WHERE NOT EXISTS (
  SELECT 1 FROM estancias
  WHERE vehiculo_placa = 'RES001' AND fecha_entrada = '2026-09-08 09:00:00'
);

-- ── Estancia ACTIVA (sin salida) — NR003 entró hace 20 min ─
INSERT INTO estancias (vehiculo_placa, fecha_entrada, fecha_salida, costo_total)
SELECT 'NR003', NOW() - INTERVAL '20 minutes', NULL, NULL
WHERE NOT EXISTS (
  SELECT 1 FROM estancias
  WHERE vehiculo_placa = 'NR003' AND fecha_salida IS NULL
);

-- ── Resumen de lo insertado ─────────────────────────────────
SELECT 'vehiculos' AS tabla, count(*) AS registros FROM vehiculos
UNION ALL SELECT 'residentes', count(*) FROM residentes
UNION ALL SELECT 'estancias',  count(*) FROM estancias;