# SCRIPT — demo-parking

**Voice:** "developer final" (voz única; proveedor/voice-id se fija en el paso de TTS)
**Voice settings:** a definir con el proveedor
**Voice direction:** Developer cercano y seguro. Explica, no vende. Frases cortas, ritmo tranquilo, deja respirar la imagen. Español neutro (México).

---

## Line 1 — Hook (Frame 1)

**Time:** 0.0 – 12.0s
**Delivery:** Arranca seco, con seguridad. La pausa antes de "Esto es Parking System" es intencional.

    Entrar, salir y cobrar un estacionamiento sin que nadie toque una tecla.
    Esto es Parking System.

## Line 2 — La idea (Frame 2)

**Time:** 12.0 – 26.0s
**Delivery:** Tono de mapa mental; señala las dos caras.

    Un mismo sistema con dos caras: el kiosko QR en la pluma,
    y un panel web para administrarlo todo desde un solo lugar.

## Line 3 — El kiosko decide (Frame 3)

**Time:** 26.0 – 54.0s
**Delivery:** Pausado, dejando ver el BIENVENIDO y el ADIOS. Marca bien "decide solo".

    El conductor muestra el QR.
    El kiosko consulta si hay una estancia activa y decide solo:
    sin estancia, abre la entrada; con estancia, cobra la salida.
    Cero teclado, cero botones.

## Line 4 — Acceso y roles (Frame 4)

**Time:** 54.0 – 72.0s
**Delivery:** Técnico pero claro.

    Del lado web el acceso es con login.
    Cada sesión viaja con un token JWT firmado,
    y el identificador del usuario va cifrado dentro.

## Line 5 — Vehículos y QR (Frame 5)

**Time:** 72.0 – 84.0s
**Delivery:** Neutro, de recorrido.

    El panel lista los vehículos registrados.
    Cada uno con su tipo —oficial, residente o no residente—,
    y su QR listo para imprimir.

## Line 6 — Alta de vehículo (Frame 6)

**Time:** 84.0 – 97.0s
**Delivery:** Ágil, "en segundos".

    Registrar un vehículo toma segundos: la placa y su tipo.
    Desde ese momento ya puede entrar al sistema.

## Line 7 — Estancias y cobro (Frame 7)

**Time:** 97.0 – 111.0s
**Delivery:** Énfasis en que el cobro es automático.

    Cada entrada y cada salida quedan registradas.
    El sistema calcula el cobro solo, según el tipo de vehículo
    y el tiempo de estancia.

## Line 8 — Pagos (Frame 8)

**Time:** 111.0 – 119.0s
**Delivery:** Rápido, de cierre de bloque.

    Y para los residentes, un informe de lo acumulado en el mes,
    con reinicio cuando toca.

## Line 9 — Seguridad por dentro (Frame 9)

**Time:** 119.0 – 139.0s
**Delivery:** El bloque más técnico; baja un poco la velocidad, deja que respire el diagrama.

    Por dentro, el identificador va cifrado con AES dentro de un token firmado.
    El superadmin vive solo en las variables de entorno,
    y solo él administra a los demás admins.
    Los servicios del kiosko quedan abiertos; el resto pide token.

## Line 10 — Usuarios y superadmin (Frame 10)

**Time:** 139.0 – 154.0s
**Delivery:** Con un dejo de remate en "ni él puede borrar su propia cuenta".

    Los admins no pueden tocar a otros usuarios:
    esa gestión es del superadmin,
    y ni él puede borrar su propia cuenta.

## Line 11 — Cierre (Frame 11)

**Time:** 154.0 – 168.0s
**Delivery:** Cierre firme; la última frase con orgullo, sin gritar.

    Angular, Spring Boot y PostgreSQL, todo en un solo JAR, desplegado y funcionando.
    Parking System: control de acceso vehicular, de punta a punta.
