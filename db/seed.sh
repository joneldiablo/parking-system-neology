#!/usr/bin/env bash
#
# Puebla la base de datos con datos de prueba (dummies).
# Idempotente: se puede ejecutar las veces que quieras.
#
# Lee las variables (DB_NAME, DB_USER...) del .env de la raíz.
# Igual que init-db.sh, soporta dos modos:
#   1. PostgreSQL en Docker  : export DOCKER_CONTAINER=<nombre>
#   2. PostgreSQL local      : export DB_ADMIN_USER=<admin> [DB_ADMIN_PASSWORD=<pw>]
#
# Uso:
#   ./db/seed.sh                          # detecta psql o contenedor automáticamente
#   DOCKER_CONTAINER=postgres ./db/seed.sh
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../.env"
SEED_SQL="$SCRIPT_DIR/seed.sql"

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

# ── Resolver cliente (psql directo o docker exec) ────────────────────────────
if [[ -n "${DOCKER_CONTAINER:-}" ]]; then
  if ! docker inspect "$DOCKER_CONTAINER" >/dev/null 2>&1; then
    echo "❌ El contenedor '$DOCKER_CONTAINER' no está corriendo."
    exit 1
  fi
  CONTAINER_ADMIN="$(docker inspect "$DOCKER_CONTAINER" --format '{{range .Config.Env}}{{println .}}{{end}}' | awk -F= '/^POSTGRES_USER=/{print $2}' | head -1)"
  ADMIN_USER="${DB_ADMIN_USER:-${CONTAINER_ADMIN:-postgres}}"
  echo "▶ Modo Docker: contenedor '$DOCKER_CONTAINER' → bd '$DB_NAME' (usuario admin: $ADMIN_USER)"
  PSQL_CMD=(docker exec -i "$DOCKER_CONTAINER" psql -U "$ADMIN_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1)
elif command -v psql >/dev/null 2>&1; then
  ADMIN_USER="${DB_ADMIN_USER:-postgres}"
  echo "▶ Modo local: psql contra $DB_HOST:$DB_PORT → bd '$DB_NAME' (usuario admin: $ADMIN_USER)"
  if [[ -n "${DB_ADMIN_PASSWORD:-}" ]]; then
    export PGPASSWORD="$DB_ADMIN_PASSWORD"
  fi
  PSQL_CMD=(psql -h "$DB_HOST" -p "$DB_PORT" -U "$ADMIN_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1)
else
  echo "❌ No hay psql en el PATH ni definiste DOCKER_CONTAINER."
  echo "   Usa: DOCKER_CONTAINER=<nombre> ./db/seed.sh"
  exit 1
fi

# ── Aplicar seed (por stdin: funciona igual en docker exec y en psql local) ──
"${PSQL_CMD[@]}" < "$SEED_SQL"

echo "✅ Datos de prueba insertados OK."