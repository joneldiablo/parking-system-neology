#!/usr/bin/env bash
# Genera el certificado autofirmado del kiosko (contexto seguro para la cámara).
# Se ignoran en git; regenera antes de levantar el proxy, o usa tus propios certs.
set -euo pipefail
cd "$(dirname "$0")"

HOST="${1:-192.168.0.80}"

openssl req -x509 -newkey rsa:2048 -nodes \
  -keyout key.pem -out cert.pem -days 365 \
  -subj "/CN=${HOST}" \
  -addext "subjectAltName=IP:${HOST},DNS:localhost,IP:127.0.0.1"

echo "Certificado generado para ${HOST} (cert.pem + key.pem)."