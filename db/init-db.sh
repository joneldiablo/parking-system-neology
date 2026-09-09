#!/usr/bin/env bash
#
# Crea la base de datos y el usuario del sistema (idempotente).
# - Si ya existen, no hace nada (no falla).
# - Soporta dos modos:
#     1. PostgreSQL en Docker  : export DOCKER_CONTAINER=<nombre>  (recomendado)
#     2. PostgreSQL local      : export DB_ADMIN_USER=<admin> [DB_ADMIN_PASSWORD=<pw>]
#
# Lee las variables (DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD) del .env
# de la raíz del proyecto. No imprime contraseñas.
#
# Uso:
#   ./db/init-db.sh                      # detecta psql o contenedor automáticamente
#   DOCKER_CONTAINER=postgres ./db/init-db.sh
#   DB_ADMIN_USER=postgres ./db/init-db.sh
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../.env"

# ── Cargar .env ──────────────────────────────────────────────────────────────
if [[ ! -f "$ENV_FILE" ]]; then
  echo "❌ No se encontró $ENV_FILE. Copia .env.example a .env primero."
  exit 1
fi
set -a; source "$ENV_FILE"; set +a

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-parking_db}"
DB_USER="${DB_USER:-parking_user}"
DB_PASSWORD="${DB_PASSWORD:-}"

# ── Resolver cliente (psql directo o docker exec) ────────────────────────────
if [[ -n "${DOCKER_CONTAINER:-}" ]]; then
  if ! docker inspect "$DOCKER_CONTAINER" >/dev/null 2>&1; then
    echo "❌ El contenedor '$DOCKER_CONTAINER' no está corriendo."
    exit 1
  fi
  CONTAINER_ADMIN="$(docker inspect "$DOCKER_CONTAINER" --format '{{range .Config.Env}}{{println .}}{{end}}' | awk -F= '/^POSTGRES_USER=/{print $2}' | head -1)"
  ADMIN_USER="${DB_ADMIN_USER:-${CONTAINER_ADMIN:-postgres}}"
  echo "▶ Modo Docker: contenedor '$DOCKER_CONTAINER' (usuario admin: $ADMIN_USER)"
  RUN_PSQL() { docker exec -i "$DOCKER_CONTAINER" psql -U "$ADMIN_USER" -d postgres -v ON_ERROR_STOP=1 "$@"; }
elif command -v psql >/dev/null 2>&1; then
  ADMIN_USER="${DB_ADMIN_USER:-postgres}"
  echo "▶ Modo local: psql contra $DB_HOST:$DB_PORT (admin: $ADMIN_USER)"
  if [[ -n "${DB_ADMIN_PASSWORD:-}" ]]; then
    export PGPASSWORD="$DB_ADMIN_PASSWORD"
  fi
  RUN_PSQL() { psql -h "$DB_HOST" -p "$DB_PORT" -U "$ADMIN_USER" -d postgres -v ON_ERROR_STOP=1 "$@"; }
else
  echo "❌ No hay psql en el PATH ni definiste DOCKER_CONTAINER."
  echo "   Usa: DOCKER_CONTAINER=<nombre> ./db/init-db.sh"
  exit 1
fi

# ── Crear usuario y base de datos si no existen ──────────────────────────────
RUN_PSQL <<SQL
-- Usuario: crear solo si no existe
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '${DB_USER}') THEN
    CREATE ROLE ${DB_USER} LOGIN PASSWORD '${DB_PASSWORD}';
    RAISE NOTICE 'Usuario ${DB_USER} creado';
  ELSE
    RAISE NOTICE 'Usuario ${DB_USER} ya existe';
  END IF;
END
\$\$;

-- Base de datos: crear solo si no existe
SELECT 'CREATE DATABASE ${DB_NAME} OWNER ${DB_USER}'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${DB_NAME}')\gexec

GRANT ALL PRIVILEGES ON DATABASE ${DB_NAME} TO ${DB_USER};
SQL

echo "✅ Base de datos '$DB_NAME' y usuario '$DB_USER' listos."