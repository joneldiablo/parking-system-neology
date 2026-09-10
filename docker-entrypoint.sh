#!/bin/sh
set -e
# Genera el cert autofirmado solo si no existe (los certs NO van versionados).
if [ ! -f /app/cert.pem ]; then
  HOST="${KIOSK_CERT_HOST:-192.168.0.80}"
  echo "Generando certificado autofirmado para ${HOST} ..."
  openssl req -x509 -newkey rsa:2048 -nodes \
    -keyout /app/key.pem -out /app/cert.pem -days 365 \
    -subj "/CN=${HOST}" \
    -addext "subjectAltName=IP:${HOST},DNS:localhost,IP:127.0.0.1"
  echo "OK (cert.pem + key.pem)"
fi
exec node /app/server.js