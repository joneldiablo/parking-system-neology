#!/usr/bin/env bash
# Genera las 11 líneas de VO con voicebox (perfil "Developer Final") - SECUENCIAL
set -u
API=http://localhost:17493
PID=7292bd3b-af1a-4e97-be84-0ca32ab970fb
OUT=/home/diablo/prueba-tecnica-neology/video/demo-parking/assets/vo
mkdir -p "$OUT"

gen() {
  local n="$1"; local text="$2"
  local resp id st
  resp=$(curl -s --max-time 120 -X POST "$API/generate" -H 'Content-Type: application/json' \
    -d "$(python3 -c 'import json,sys;print(json.dumps({"profile_id":sys.argv[1],"text":sys.argv[2],"language":"es","engine":"qwen","model_size":"1.7B"}))' "$PID" "$text")")
  id=$(echo "$resp" | python3 -c 'import json,sys;print(json.load(sys.stdin).get("id",""))' 2>/dev/null)
  if [ -z "$id" ]; then echo "L$n ERROR POST: $resp"; return 1; fi
  echo "L$n id=$id"
  for i in $(seq 1 180); do
    st=$(curl -s --max-time 20 "$API/generate/$id/status" | sed 's/^data: //' | python3 -c 'import json,sys;d=json.load(sys.stdin);print(d.get("status"),round(d.get("duration") or 0,2),d.get("error") or "")' 2>/dev/null)
    case "$st" in
      completed*)
        src="/mnt/480ssd/voicebox/data/generations/${id}.wav"
        if [ -f "$src" ]; then cp "$src" "$OUT/vo${n}.wav"; echo "L$n OK $st"; return 0; fi
        echo "L$n OK pero sin wav ($src)"; return 1;;
      failed*|error*)
        echo "L$n FAIL $st"; return 1;;
    esac
    sleep 5
  done
  echo "L$n TIMEOUT"
  return 1
}

gen 1 "Entrar, salir y cobrar un estacionamiento sin que nadie toque una tecla. Esto es Parking System."
gen 2 "Un mismo sistema con dos caras: el kiosko QR en la pluma, y un panel web para administrarlo todo desde un solo lugar."
gen 3 "El conductor muestra el QR. El kiosko consulta si hay una estancia activa y decide solo: sin estancia, abre la entrada; con estancia, cobra la salida. Cero teclado, cero botones."
gen 4 "Del lado web el acceso es con login. Cada sesión viaja con un token JWT firmado, y el identificador del usuario va cifrado dentro."
gen 5 "El panel lista los vehículos registrados. Cada uno con su tipo, oficial, residente o no residente, y su QR listo para imprimir."
gen 6 "Registrar un vehículo toma segundos: la placa y su tipo. Desde ese momento ya puede entrar al sistema."
gen 7 "Cada entrada y cada salida quedan registradas. El sistema calcula el cobro solo, según el tipo de vehículo y el tiempo de estancia."
gen 8 "Y para los residentes, un informe de lo acumulado en el mes, con reinicio cuando toca."
gen 9 "Por dentro, el identificador va cifrado con AES dentro de un token firmado. El superadmin vive solo en las variables de entorno, y solo él administra a los demás admins. Los servicios del kiosko quedan abiertos; el resto pide token."
gen 10 "Los admins no pueden tocar a otros usuarios: esa gestión es del superadmin, y ni él puede borrar su propia cuenta."
gen 11 "Angular, Spring Boot y PostgreSQL, todo en un solo JAR, desplegado y funcionando. Parking System: control de acceso vehicular, de punta a punta."

echo "=== DURACIONES ==="
for f in "$OUT"/vo*.wav; do
  [ -f "$f" ] || continue
  d=$(ffprobe -v error -show_entries format=duration -of csv=p=0 "$f")
  echo "$(basename "$f") $d"
done
echo "DONE"
