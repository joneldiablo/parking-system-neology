#!/usr/bin/env bash
set -u
API=http://localhost:17493
PID=7292bd3b-af1a-4e97-be84-0ca32ab970fb
OUT=/home/diablo/prueba-tecnica-neology/video/demo-parking/assets/vo
declare -A T=(
 [3]="El conductor muestra el QR. El kiosko consulta si hay una estancia activa y decide solo: sin estancia, abre la entrada; con estancia, cobra la salida. Cero teclado, cero botones."
 [4]="Del lado web el acceso es con login. Cada sesión viaja con un token JWT firmado, y el identificador del usuario va cifrado dentro."
 [5]="El panel lista los vehículos registrados. Cada uno con su tipo, oficial, residente o no residente, y su QR listo para imprimir."
 [6]="Registrar un vehículo toma segundos: la placa y su tipo. Desde ese momento ya puede entrar al sistema."
 [7]="Cada entrada y cada salida quedan registradas. El sistema calcula el cobro solo, según el tipo de vehículo y el tiempo de estancia."
 [8]="Y para los residentes, un informe de lo acumulado en el mes, con reinicio cuando toca."
 [9]="Por dentro, el identificador va cifrado con AES dentro de un token firmado. El superadmin vive solo en las variables de entorno, y solo él administra a los demás admins. Los servicios del kiosko quedan abiertos; el resto pide token."
 [10]="Los admins no pueden tocar a otros usuarios: esa gestión es del superadmin, y ni él puede borrar su propia cuenta."
 [11]="Angular, Spring Boot y PostgreSQL, todo en un solo JAR, desplegado y funcionando. Parking System: control de acceso vehicular, de punta a punta."
)

# asegurar modelo cargado
curl -s --max-time 30 -X POST "$API/models/load" -H 'Content-Type: application/json' -d '{"model_name":"qwen-tts-1.7B"}' >/dev/null 2>&1
echo "watchdog: $(curl -s --max-time 15 -X POST $API/watchdog/disable)"

gen1() {
  local n="$1" text="$2" resp id st attempt
  for attempt in 1 2 3 4; do
    resp=$(curl -s --max-time 60 -X POST "$API/generate" -H 'Content-Type: application/json' \
      -d "$(python3 -c 'import json,sys;print(json.dumps({"profile_id":sys.argv[1],"text":sys.argv[2],"language":"es","engine":"qwen","model_size":"1.7B"}))' "$PID" "$text")" 2>/dev/null)
    id=$(echo "$resp" | python3 -c 'import json,sys;print(json.load(sys.stdin).get("id",""))' 2>/dev/null)
    if [ -z "$id" ]; then echo "L$n intento$attempt sin id"; sleep 20; continue; fi
    for i in $(seq 1 90); do
      st=$(curl -s --max-time 20 "$API/generate/$id/status" 2>/dev/null | sed 's/^data: //' | python3 -c 'import json,sys;d=json.load(sys.stdin);print(d.get("status"),round(d.get("duration") or 0,2),d.get("error") or "")' 2>/dev/null)
      case "$st" in
        completed*)
          if [ -f "/mnt/480ssd/voicebox/data/generations/${id}.wav" ]; then
            cp "/mnt/480ssd/voicebox/data/generations/${id}.wav" "$OUT/vo${n}.wav"
            echo "L$n OK intento$attempt $st"; return 0
          fi
          echo "L$n sin wav"; break;;
        failed*|error*) echo "L$n intento$attempt FAIL: $st"; break;;
      esac
      sleep 4
    done
    echo "L$n reintentando..."; sleep 15
    curl -s --max-time 30 -X POST "$API/models/load" -H 'Content-Type: application/json' -d '{"model_name":"qwen-tts-1.7B"}' >/dev/null 2>&1
  done
  echo "L$n AGOTADO"; return 1
}

for n in 3 4 5 6 7 8 9 10 11; do
  [ -f "$OUT/vo${n}.wav" ] && { echo "L$n ya existe"; continue; }
  gen1 "$n" "${T[$n]}"
done
echo "=== DURACIONES ==="
for f in "$OUT"/vo*.wav; do [ -f "$f" ] || continue; echo "$(basename "$f") $(ffprobe -v error -show_entries format=duration -of csv=p=0 "$f")"; done
echo "DONE"
