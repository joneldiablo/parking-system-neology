#!/usr/bin/env bash
# Mezcla SFX de UI (whoosh/click/chime) sobre el render con VO + música.
# Uso: bash scripts/mix-sfx.sh <in.mp4> <out.mp4>
set -euo pipefail
cd "$(dirname "$0")/.."

IN="${1:-renders/demo-parking-final.mp4}"
OUT="${2:-renders/demo-parking-sfx.mp4}"

# Puntos de sección (seg) — cortes entre escenas
BOUNDS=(16 34 48 54 62 79 92 99 125 141)
# Clicks UI (confirmaciones / acciones)
CLICKS=(16 48 92 125)
# Chimes (acceso concedido @34, pago registrado @96)
CHIMES=(34 96)

F="[1:a]asplit=${#BOUNDS[@]}$(printf '[w%d]' $(seq 1 ${#BOUNDS[@]}));"
MIX=""
i=0
for t in "${BOUNDS[@]}"; do
  i=$((i+1))
  ms=$((t*1000))
  F+="[w$i]adelay=${ms}|${ms},volume=0.38[wh$i];"
  MIX+="[wh$i]"
done

F+="[2:a]asplit=${#CLICKS[@]}$(printf '[c%d]' $(seq 1 ${#CLICKS[@]}));"
i=0
for t in "${CLICKS[@]}"; do
  i=$((i+1))
  ms=$((t*1000+120))
  F+="[c$i]adelay=${ms}|${ms},volume=0.55[cl$i];"
  MIX+="[cl$i]"
done

F+="[3:a]asplit=${#CHIMES[@]}$(printf '[k%d]' $(seq 1 ${#CHIMES[@]}));"
i=0
for t in "${CHIMES[@]}"; do
  i=$((i+1))
  ms=$((t*1000))
  F+="[k$i]adelay=${ms}|${ms},volume=0.5[ck$i];"
  MIX+="[ck$i]"
done

N=$(( ${#BOUNDS[@]} + ${#CLICKS[@]} + ${#CHIMES[@]} ))
F+="[0:a]${MIX}amix=inputs=$((N+1)):duration=first:normalize=0[mixed];"
F+="[mixed]loudnorm=I=-16:TP=-1.5:LRA=11[aout]"

ffmpeg -v error -y -i "$IN" -i assets/sfx/whoosh.wav -i assets/sfx/click.wav -i assets/sfx/chime.wav \
  -filter_complex "$F" -map 0:v -map "[aout]" -c:v copy -c:a aac -b:a 192k -ar 48000 -movflags +faststart "$OUT"

echo "OK -> $OUT"
ffprobe -v error -show_entries format=duration,size -of default=noprint_wrappers=1 "$OUT"
