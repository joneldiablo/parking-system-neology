-- Seed inicial - PostgreSQL
-- Ejecutar después de iniciar el backend por primera vez

-- Los vehículos se crean desde la API, este archivo es solo como referencia
-- de datos de prueba que podrías insertar directamente en la BD

INSERT INTO vehiculos (placa, tipo) VALUES
  ('ABC123', 'OFICIAL'),
  ('XYZ789', 'RESIDENTE'),
  ('DEF456', 'NO_RESIDENTE')
ON CONFLICT (placa) DO NOTHING;

INSERT INTO residentes (placa, tiempo_acumulado_minutos, ultimo_reinicio) VALUES
  ('XYZ789', 0, NOW())
ON CONFLICT (placa) DO NOTHING;
