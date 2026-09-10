---
format: 1920x1080
duration: 2m55s
message: "Un estacionamiento que decide solo: registra, cobra y se administra en un solo sistema"
arc: Hook → Idea → Kiosko → Panel → Dinero → Seguridad → Cierre
audience: evaluadores técnicos y reclutadores de la prueba técnica
mode: collaborative
---

## Frame 1 — Hook

- scene: Título de entrada sobre rejilla de estacionamiento animada
- duration: 12s
- transition_in: cut
- status: outline
- poster: 6s
- blueprint: kinetic-type-beats
- voiceover: "Entrar, salir y cobrar un estacionamiento sin que nadie toque una tecla. Esto es Parking System."
- src: compositions/frames/01-hook.html

Abre en frío con la promesa en lenguaje de resultado, no con la lista de
features. Archivo Black grande, acento azul, rejilla de fondo respirando.

## Frame 2 — La idea

- scene: Diagrama "un sistema, dos caras" (kiosko ↔ API ↔ panel ↔ base de datos)
- duration: 14s
- transition_in: crossfade
- status: outline
- poster: 7s
- blueprint: constellation-hub
- voiceover: "Un mismo sistema con dos caras: el kiosko QR en la pluma y un panel web para administrarlo todo desde un solo lugar."
- src: compositions/frames/02-idea.html

Nodos (Kiosko, API, Panel, PostgreSQL) orbitando un núcleo. Es el mapa mental
que el resto del video recorre. Story-spine §4: los nodos son los reales.

## Frame 3 — El kiosko decide

- scene: Kiosko QR en acción: BIENVENIDO y luego ADIOS con cobro
- duration: 28s
- transition_in: cut
- status: outline
- poster: 12s
- blueprint: device-surface-showcase
- media: assets/kiosk-demo.mp4
- media_start: 1.5s
- media_end: 23s
- voiceover: "El conductor muestra el QR. El kiosko consulta si hay una estancia activa y decide solo: sin estancia, abre la entrada; con estancia, cobra la salida. Cero teclado, cero botones."
- src: compositions/frames/03-kiosk.html

La propia tarjeta oscura del kiosko es el set. Lower-thirds marcan
"ENTRADA · BIENVENIDO" y "SALIDA · COBRO". El verde del kiosko es el único uso
de success en el video.

## Frame 4 — Acceso y roles

- scene: Login del panel admin; token JWT en cabecera
- duration: 18s
- transition_in: crossfade
- status: outline
- poster: 8s
- blueprint: cursor-ui-demo
- media: assets/admin-demo.mp4
- media_start: 2s
- media_end: 21s
- voiceover: "Del lado web el acceso es con login. Cada sesión viaja con un token JWT firmado, y el identificador del usuario va cifrado dentro."
- src: compositions/frames/04-login.html

Chip "JWT · HMAC" y "AES/GCM" aparecen mientras se escribe. El footage va en
tarjeta surface con borde hairline.

## Frame 5 — Vehículos y QR

- scene: Listado de vehículos con tipos y botón de QR
- duration: 12s
- transition_in: cut
- status: outline
- poster: 5s
- blueprint: grid-card-assemble
- media: assets/admin-demo.mp4
- media_start: 22s
- media_end: 26.5s
- voiceover: "El panel lista los vehículos registrados. Cada uno con su tipo —oficial, residente o no residente— y su QR listo para imprimir."
- src: compositions/frames/05-vehiculos.html

Callout al botón QR. Chip "QR /neo/qr/{placa}".

## Frame 6 — Alta de vehículo

- scene: Formulario de alta eligiendo tipo (no residente)
- duration: 13s
- transition_in: cut
- status: outline
- poster: 6s
- blueprint: cursor-ui-demo
- media: assets/admin-demo.mp4
- media_start: 27s
- media_end: 40s
- voiceover: "Registrar un vehículo toma segundos: la placa y su tipo. Desde ese momento ya puede entrar al sistema."
- src: compositions/frames/06-alta.html

## Frame 7 — Estancias y cobro

- scene: Entrada registrada, salida cobrada con costo calculado
- duration: 14s
- transition_in: cut
- status: outline
- poster: 7s
- blueprint: dataviz-countup
- media: assets/admin-demo.mp4
- media_start: 44s
- media_end: 56s
- voiceover: "Cada entrada y cada salida quedan registradas. El sistema calcula el cobro solo, según el tipo de vehículo y el tiempo de estancia."
- src: compositions/frames/07-estancias.html

El costo calculado es el foco: un chip con el monto hace count-up cuando aparece.

## Frame 8 — Pagos

- scene: Informe de pagos de residentes con montos acumulados
- duration: 8s
- transition_in: cut
- status: outline
- poster: 4s
- blueprint: grid-card-assemble
- media: assets/admin-demo.mp4
- media_start: 54s
- media_end: 60s
- voiceover: "Y para los residentes, un informe de lo acumulado en el mes, con reinicio cuando toca."
- src: compositions/frames/08-pagos.html

## Frame 9 — Seguridad por dentro

- scene: Diagrama de doble capa (AES/GCM dentro de JWT firmado) + roles y endpoints abiertos
- duration: 20s
- transition_in: crossfade
- status: outline
- poster: 10s
- blueprint: grid-card-assemble
- voiceover: "Por dentro, el identificador va cifrado con AES dentro de un token firmado. El superadmin vive solo en las variables de entorno, y solo él administra a los demás admins. Los servicios del kiosko quedan abiertos; el resto pide token."
- src: compositions/frames/09-seguridad.html

Tarjetas que se ensamblan: "ID CIFRADO · AES/GCM", "TOKEN FIRMADO · HMAC-SHA256",
"SUPERADMIN · solo .env", "KIOSKO · endpoints abiertos". La escena de más peso
técnico, para el evaluador.

## Frame 10 — Usuarios y superadmin

- scene: CRUD de administradores, alta de un admin nuevo
- duration: 15s
- transition_in: cut
- status: outline
- poster: 7s
- blueprint: cursor-ui-demo
- media: assets/admin-demo.mp4
- media_start: 58.5s
- media_end: 74s
- voiceover: "Los admins no pueden tocar a otros usuarios: esa gestión es del superadmin, y ni él puede borrar su propia cuenta."
- src: compositions/frames/10-usuarios.html

## Frame 11 — Cierre

- scene: Stack (Angular · Spring Boot · PostgreSQL · Docker) y cierre de marca
- duration: 14s
- transition_in: crossfade
- status: outline
- poster: 7s
- blueprint: logo-assemble-lockup
- voiceover: "Angular, Spring Boot y PostgreSQL, todo en un solo JAR, desplegado y funcionando. Parking System: control de acceso vehicular, de punta a punta."
- src: compositions/frames/11-cierre.html

Cierra con el repositorio y un wordmark "PARKING SYSTEM" que se arma. Última
palabra en pantalla sostenida.
