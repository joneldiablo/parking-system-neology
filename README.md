# Prueba Técnica Full-Stack – Neology

Sistema web de **gestión de acceso vehicular a un estacionamiento**:

- Registro de entradas y salidas de vehículos.
- Cálculo automático del cobro según el tipo de vehículo.
- Reporte mensual de pagos de residentes.
- Reinicio de mes.

> Es un **monorepo**: backend y frontend viven en un mismo repositorio, con
> build diferenciado para **desarrollo** (separados) y **producción** (un solo JAR).

---

## Tecnologías

### Backend (`backend/`)
| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17+ (proyecto compilado con JDK 21 LTS) | Lenguaje |
| Spring Boot | 3.2.5 | Framework web (REST + servidor embebido) |
| Maven | 3.9+ | Gestión de dependencias y build |
| PostgreSQL | 15/16 | Base de datos |
| JPA / Hibernate | — | Persistencia (ORM) |
| Lombok | — | Reducción de boilerplate |
| Swagger (SpringDoc) | 2.5.0 | Documentación de la API |

### Frontend (`frontend/`)
| Tecnología | Versión | Uso |
|---|---|---|
| Angular | 16 | Framework de frontend |
| Angular Material | — | ~~UI~~ *(reemplazado por Bootstrap)* |
| Bootstrap | 5.3.3 | UI (compilado vía SCSS) |
| SCSS | — | Estilos (Bootstrap se compila desde fuente SCSS) |
| TypeScript | ~5.1 | Lenguaje |
| Jasmine + Karma | — | Pruebas unitarias |

---

## Dependencias externas (NO incluidas en el repo)

El repositorio **no trae** las herramientas de build; debes tenerlas instaladas en tu máquina:

| Dependencia | Versión requerida | Se usa para | Cómo saber si la tienes |
|---|---|---|---|
| **Node.js** (con npm) | **18+ LTS** (recomendado 20/22 LTS; probado con 24) | Compilar el frontend Angular y Bootstrap vía `npm run build` | `node -v` y `npm -v` |
| **Java (JDK)** | **17 o superior** (se recomienda 21 LTS) | Compilar y ejecutar el backend | `java -version` |
| **Maven** | **3.9+** | Empaquetar el backend (en producción compila también el frontend) | `mvn -version` |
| **PostgreSQL** | 15+ (server o contenedor Docker) | Base de datos del sistema | `psql --version` o un contenedor |

> ⚠️ **Only LINUX/MAC**: los comandos asumen bash. En Windows usar WSL o Cmder.

> ℹ️ Todo el resto de dependencias (Spring Boot, Angular CLI, Bootstrap, etc.)
> **se descargan automáticamente**: Maven desde Maven Central (`.m2`), npm desde
> npm registry (`node_modules`).

### ¿Cómo se instala Node? (si no lo tienes)

- Instalador oficial: https://nodejs.org (descargar LTS).
- O con **nvm** (Linux/Mac): `curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash` y luego `nvm install --lts`.

### ¿Cómo se instala Java + Maven? (si no los tienes)

- **JDK 21 (Temurin)**: https://adoptium.net/
- **Maven 3.9+**: https://maven.apache.org/download.cgi (descomprimir y agregar `bin/` al `PATH`).

---

## Estructura del proyecto

```
prueba-tecnica-neology/
│
├── backend/                              # API REST Spring Boot
│   ├── pom.xml                           # Perfil `prod` compila el frontend automáticamente
│   └── src/
│       ├── main/
│       │   ├── java/com/neology/parking/
│       │   │   ├── ParkingApplication.java   # Punto de entrada
│       │   │   ├── controller/               # Endpoints REST (/neo/**)
│       │   │   ├── service/                  # Reglas de negocio (tarifas, estancias)
│       │   │   ├── repository/               # Acceso a datos (JPA)
│       │   │   ├── model/                    # Entidades: Vehiculo, Estancia, Residente
│       │   │   ├── dto/                      # Objetos de transferencia (validación incluida)
│       │   │   └── config/                   # CORS + fallback SPA (servir rutas de Angular)
│       │   └── resources/
│       │       ├── application.properties    # Lee variables del .env (spring-dotenv)
│       │       └── static/                   # ⚠️ Se genera en `npm run build` (no se commitea)
│       └── test/                             # Pruebas unitarias Spring Boot (H2)
│
├── frontend/                             # App Angular
│   ├── package.json                      # Dependencias del frontend
│   ├── angular.json                      # outputPath → backend/src/main/resources/static
│   ├── karma.conf.js                     # Config de pruebas (Chrome headless)
│   └── src/
│       ├── environments/environment*.ts  # apiUrl → '/neo' (relativo, misma origen en prod)
│       ├── proxy.conf.json               # Dev: redirige /neo → http://localhost:8082
│       ├── test.ts                       # Bootstrap de Jasmine
│       └── app/
│           ├── components/               # VehiculoList, EstanciaForm, VehiculoForm, PagoReport
│           └── services/parking.service.ts   # Cliente HTTP de la API
│
├── kiosk/                                # 📷 App del kiosko (Car QR), se copia al JAR en /kiosk
│   └── index.html                        # Escáner de QR para entrada/salida (vanilla, sin build)
│
├── docker-compose.yml                   # (Opcional) PostgreSQL sin instalar Postgres
├── db/                                  # Scripts de base de datos
│   ├── init-db.sh                       # Crea BD + usuario si no existen (idempotente)
│   ├── seed.sh                          # Aplica los dummies
│   └── seed.sql                         # Datos de prueba (vehículos, residentes, estancias)
├── .env                                 # Variables locales (se crea desde .env.example)
├── .env.example                         # Plantilla de variables (si se sube al repo)
└── README.md
```

---

## Configuración inicial

### 1. Variables de entorno

```bash
cp .env.example .env
# edita .env con tus credenciales reales si difieren
```

El backend lee el `.env` automáticamente (dependencia **spring-dotenv**), así que
**no hay que exportar variables a mano**.

```dotenv
DB_HOST=localhost
DB_PORT=5432
DB_NAME=parking_db
DB_USER=parking_user
DB_PASSWORD=parking_secret_2025

SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8082        # ⚠️ 8080 suele estar ocupado por otros servicios
API_URL=http://localhost:8082
```

### 2. Base de datos PostgreSQL

Opciones:

**A) Con Docker (recomendado — no instala Postgres en tu sistema):**
```bash
docker compose up -d
```
Crea la base `parking_db` con usuario `parking_user` (puerto `5432`).

**B) Con PostgreSQL ya instalado o con otro contenedor** — usa el script automático:
```bash
# Si el contenedor Docker que tiene PostgreSQL está corriendo:
DOCKER_CONTAINER=postgres ./db/init-db.sh

# Si tienes PostgreSQL local (psql) pide el usuario administrador:
DB_ADMIN_USER=postgres ./db/init-db.sh
```
> El script lee las credenciales del `.env`, y **solo crea la base y el usuario si
> no existen** (es idempotente: se puede ejecutar las veces que quieras).

O manualmente:

```sql
CREATE DATABASE parking_db;
CREATE USER parking_user WITH PASSWORD 'parking_secret_2025';
GRANT ALL PRIVILEGES ON DATABASE parking_db TO parking_user;
```

---

## 🛠️ Comandos útiles

### Modo DESARROLLO (cambios en caliente, frontend y backend separados)

| Qué | Comando | Dónde |
|---|---|---|
| Arrancar el backend | `mvn spring-boot:run` | `backend/` |
| Arrancar el frontend (hot reload) | `npm start` | `frontend/` |
| Frontend | http://localhost:4200 | — |
| Backend / API | http://localhost:8082 | — |
| Swagger | http://localhost:8082/swagger-ui.html | — |

> En este modo `ng serve` usa `src/proxy.conf.json`: **todas las llamadas `/neo/*`**
> se redirigen al backend en `:8082` → **no hace falta CORS ni URL en el frontend**
> (la api es relativa `/neo`).

### Modo PRODUCCIÓN (un solo comando, un solo JAR, un solo puerto)

| Qué | Comando | Dónde |
|---|---|---|
| Empaquetar todo (frontend + backend) | `mvn -Pprod clean package` | `backend/` |
| Ejecutar | `java -jar target/parking-system-1.0.0.jar` | `backend/` |
| Sistema completo | http://localhost:8082 | — |
| Swagger | http://localhost:8082/swagger-ui.html | — |

> Con `-Pprod`, Maven **ejecuta automáticamente** `npm install --include=dev` y
> `npm run build` del frontend (publica el build en
> `backend/src/main/resources/static/`), y luego empaqueta **un único JAR**
> (un ZIP ejecutable con servidor + API + frontend dentro) que se sirve junto.
>
> ⚠️ El modo `prod` **necesita Node.js** en el PATH, porque Maven invoca `npm`.

### Otros comandos

| Qué | Comando |
|---|---|
| Regenerar el build del frontend hacia el backend | `npm run build` (en `frontend/`) |
| Pruebas backend | `mvn test` (en `backend/`) |
| Pruebas frontend (Jasmine + Karma) | `npm test` (en `frontend/`) |
| Operar con la base de datos | ver abajo (sección *Datos de prueba*) |
| Limpiar build | `mvn clean` / `rm -rf node_modules dist` |

> ℹ️ **Tip si `ng` no se encuentra después de `npm install`:** puede deberse a
> `NODE_ENV=production` en tu shell (npm omite las devDependencies).
> Solución: `unset NODE_ENV && npm install --include=dev`.

---

## 🧪 Datos de prueba (dummies)

La carpeta `db/` trae scripts para preparar la base de datos:

| Script | Qué hace |
|---|---|
| `./db/init-db.sh` | Crea la base `parking_db` y el usuario si no existen |
| `./db/seed.sh` | Inserta datos de prueba (idempotente) |
| `./db/seed.sql` | El SQL de los datos de prueba |

```bash
# 1. Crear BD/usuario si hace falta (igual que init-db, admite modos docker/local)
DOCKER_CONTAINER=postgres ./db/init-db.sh

# 2. Poblar con dummies
DOCKER_CONTAINER=postgres ./db/seed.sh
```

> Ambos usan el `.env` de la raíz y son **idempotentes**: ejecutarlos varias veces
> no duplica ni borra nada.

### Los dummies insertados

**Vehículos:** `OFI001`, `OFI002` (oficial) · `RES001`, `RES002` (residente) · `NR001`, `NR002`, `NR003` (no residente).

**Residentes acumulados:**
| Placa | Minutos acumulados | Monto en reporte |
|---|---|---|
| RES001 | 1520 (≈25,3 h) | $76.00 |
| RES002 | 480 (≈8 h) | $24.00 |

**Estancias:**
| Vehículo | Entrada → Salida | Costo | Nota |
|---|---|---|---|
| NR001 | 08-sep 09:00 → 09:45 | $22.50 | no residente (45 min × $0.50) |
| NR002 | 08-sep 10:00 → 11:30 | $45.00 | no residente (90 min × $0.50) |
| OFI001 | 08-sep 08:00 → 17:00 | $0 | oficial |
| RES001 | 08-sep 09:00 → 09:30 | $0 | residente (acumula, no cobra) |
| NR003 | hace 20 min (activa) | — | estancia sin salida todavía |

Al levantar el sistema, la vista **Pagos** (`/pagos`) mostrará el reporte de
residentes con los montos de la tabla anterior.

---

## 📷 Kiosko de acceso (Car QR)

Frontend extra **desacoplado** (`kiosk/index.html`): en el build de producción se
**copia dentro del mismo JAR** (ruta `/kiosk`) junto con el admin, pero como es un
archivo HTML suelto también puede servirse aparte (otro puerto/servidor estático) si
algún día se quiere desplegar por separado.

Frente a la pluma de acceso: el conductor muestra un **QR pegado al parabrisas** y el
kiosko (una cámara + pantalla) registra la entrada o la salida automáticamente. No
toca teclado ni botones: la aplicación decide sola, consumiendo la misma API `/neo`.

- **El QR codifica solo la placa** (p. ej. `NR001`), simple y a prueba de
  desactualizaciones: si el vehículo cambia de tipo, el mismo QR sigue valiendo
  porque la tarifa se resuelve en el backend.
- **Cómo decide entrada o salida:** consulta `GET /neo/estancias/{placa}`; si hay una
  estancia **sin** `fechaSalida` → es salida (`POST /neo/estancias/salida`), si no → entrada
  (`POST /neo/estancias/entrada`). Pantalla completa verde "BIENVENIDO" / "ADIOS $X", o roja si el QR no es válido o el vehículo no está registrado. Sonido por WebAudio.
- **Stack:** HTML/CSS/JS puro (sin build) + `html5-qrcode` para la cámara + Bootstrap 5 de CDN.
  En `GET /neo/qr/{placa}` el backend (ZXing) genera el PNG a imprimir.

### Generar e imprimir los QR

En la app admin → **Vehículos Registrados** hay un botón **QR** por cada fila que
descarga `PLACA-qr.png`. También por consola:

```bash
curl -o NR001-qr.png http://localhost:8082/neo/qr/NR001
```

### Probar el kiosko

Dos formas, según cómo lo quieras servir:

**A) Dentro del JAR (todo junto)** — el build `prod` copia `kiosk/index.html` al
jár; queda disponible en la misma app:

```bash
mvn -Pprod clean package                        # de la raíz
java -jar backend/target/parking-system-1.0.0.jar
# abre: http://localhost:8082/kiosk/            <- kiosko
#       http://localhost:8082/                  <- admin
```

**B) Desacoplado (app aparte)** — sirve el directorio `kiosk/` con tu servidor
favorito apuntando a la API en `:8082`:

```bash
# terminal 1: backend
java -jar backend/target/parking-system-1.0.0.jar
# terminal 2: kiosko
python3 -m http.server 8090 --directory kiosk   # abre http://localhost:8090
```

**C) Todo en un solo HTTPS (LAN, recomendado para kiosko)** — el puerto seguro
`:8443` hace de **puenteo** hacia el JAR (`:8082`): sirve admin, kiosko y API en
un único origen seguro, necesario para que el navegador pida permiso de cámara
(Chrome/Brave solo exponen `getUserMedia` en `https`/`localhost`).

```bash
# 1) generar el certificado autofirmado (una vez; la IP de tu máquina en la LAN)
./gen-cert.sh 192.168.0.80            # crea cert.pem + key.pem en la raíz (ignorados en git)

# 2) backend normal
java -jar backend/target/parking-system-1.0.0.jar

# 3) proxy HTTPS todo-en-uno -> :8082
KIOSK_UPSTREAM=127.0.0.1:8082 node server.js
#    -- o por defecto ya usa 127.0.0.1:8082 en :8443

# abre (acepta el certificado autofirmado una vez):
#   https://192.168.0.80:8443/           <- admin
#   https://192.168.0.80:8443/kiosk/     <- kiosko
```

El proxy además: hace **SPA fallback** para rutas profundas del admin
(recargar `/vehiculos` no da 404), sirve `/favicon.ico` y mantiene el
redirect `/kiosk` relativo (no salta al host interno `:8082`).

> Si la cámara no está disponible (escritorios/headless), el kiosko muestra el motivo
> y queda activo un **campo manual** (abajo a la izquierda) para digitar la placa y
> seguir el mismo flujo. Los orígenes ``http://localhost:4200``, ``http://localhost:8090``,
> la LAN (`http(s)://192.168.0.*:*`) y `https://neo.diablitodevops.com` están permitidos
> en CORS (`app.cors.allowed-origin-patterns`, sobreescribible con la env
> `CORS_ALLOWED_ORIGIN_PATTERNS`).

> ⚠️ **No quites el dominio del CORS list**: los scripts `type="module"` de Angular
> (`runtime/polyfills/main*.js`) se cargan en **modo CORS** y si el `Origin` no está
> permitido el navegador recibe **403** en esos archivos aunque el HTML cargue bien
> (curl siempre responde 200 sin cabecera `Origin`, por eso no lo detectas por curl).
> La app da 200 con `Origin: https://neo.diablitodevops.com` cuando el patrón está
> presente.

---

## 🐳 Docker (producción)

Opcional: la app empaquetada en contenedores (`docker compose`).

```bash
docker compose up --build -d          # db (postgres:16) + app (jar :8082) + proxy (https :8443)
```

- `app` construye el **JAR en multi-stage** (Maven + Node compilan frontend, el
  runtime solo lleva JRE). El contenedor **no** incluye certs TLS: el copy de
  `kiosk/` al JAR excluye `*.pem` / `*.key`.
- `proxy` reutiliza `server.js` y **genera un certificado autofirmado en el
  primer arranque** (var `KIOSK_CERT_HOST`, la IP pública de tu máquina; los
  certs van ignorados en git). Sustituye esos certs por los tuyos en producción.
- Config por variables de entorno (`DB_HOST`, `DB_PASSWORD`, `SERVER_PORT`…).

---

## 🔐 Autenticación y seguridad

El panel admin exige **login con JWT** y maneja dos roles: `SUPERADMIN` y `ADMIN`.

- `POST /api/auth/login` valida credenciales y devuelve el **token** (más `username` y `rol`).
- El token se guarda en **`sessionStorage`** (se pierde al cerrar la pestaña) y viaja
  en la cabecera `Authorization: Bearer <token>`.
- El **superadmin solo se siembra desde variables de entorno**
  (`SUPERADMIN_USER` / `SUPERADMIN_PASSWORD`) al arrancar; no existe en el código.
- El CRUD de usuarios (`/api/usuarios/**`) es **exclusivo del `SUPERADMIN`**; los
  `ADMIN` solo administran vehículos/estancias y reciben `403` en esa ruta.
- El `id` del usuario viaja **cifrado (AES/GCM)** dentro del JWT, que además va firmado
  (HMAC-SHA256): **doble capa**. El JWT no es legible ni manipulable sin la clave.

### Servicios abiertos para el kiosko QR

Por diseño, el **kiosko físico** no puede autenticarse, así que estos servicios
quedan **sin token** (ver `SecurityConfig`):

| Método | Endpoint | Uso |
|--------|----------|-----|
| POST | `/neo/estancias/entrada` | Registrar entrada por QR |
| POST | `/neo/estancias/salida` | Registrar salida por QR |
| GET | `/neo/estancias/{placa}` | Consultar si hay una estancia activa |
| GET | `/neo/qr/{placa}` | Generar el PNG del QR |

**Todo lo demás requiere token**: `/neo/vehiculos`, `/neo/estancias` (listado),
`/neo/residentes/pagos`, `/neo/mes/iniciar` y toda la gestión de usuarios
(`/api/usuarios/**`, `/api/auth/me`). Un recurso protegido sin token responde
`401`; con rol insuficiente, `403`.

---

## API REST (`/neo`)

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/neo/vehiculos/oficiales` | Alta de vehículo oficial (no paga) | 🔒 |
| POST | `/neo/vehiculos/residentes` | Alta de vehículo residente | 🔒 |
| POST | `/neo/vehiculos/no-residentes` | Alta de vehículo no residente | 🔒 |
| POST | `/neo/estancias/entrada` | Registrar entrada de vehículo | 🔓 kiosko |
| POST | `/neo/estancias/salida` | Registrar salida (calcula cobro) | 🔓 kiosko |
| GET | `/neo/residentes/pagos` | Informe de pagos de residentes | 🔒 |
| POST | `/neo/mes/iniciar` | Reiniciar mes (re)establecer estancias y residentes | 🔒 |
| GET | `/neo/vehiculos` | Listar todos los vehículos | 🔒 |
| GET | `/neo/estancias` | Listar todas las estancias | 🔒 |
| GET | `/neo/estancias/{placa}` | Estancias de un vehículo | 🔓 kiosko |
| GET | `/neo/qr/{placa}` | Imagen PNG con el QR de una placa registrada | 🔓 kiosko |

> 🔒 requiere `Authorization: Bearer <token>` · 🔓 abierto (kiosko QR)

### Ejemplos con curl

```bash
# Alta de un vehículo residante
curl -X POST http://localhost:8082/neo/vehiculos/residentes \
     -H "Content-Type: application/json" \
     -d '{"placa":"XYZ789"}'

# Registrar entrada
curl -X POST http://localhost:8082/neo/estancias/entrada \
     -H "Content-Type: application/json" -d '{"placa":"XYZ789"}'

# Registrar salida → devuelve costo calculado
curl -X POST http://localhost:8082/neo/estancias/salida \
     -H "Content-Type: application/json" -d '{"placa":"XYZ789"}'
```

---

## Reglas de negocio (tarifas)

| Tipo de vehículo | Tarifa | Detalle |
|---|---|---|
| **Oficial** | **$0** | No paga nada |
| **Residente** | **$0.05/min** | El tiempo se **acumula mes a mes** hasta el reinicio |
| **No residente** | **$0.50/min** | Se cobra al salir, según la duración de la estancia |

- Un vehículo **debe estar dado de alta** antes de registrar una entrada.
- No se permite una segunda entrada sin salida previa (estancia activa).

---

## Pruebas (33 en total: 22 backend + 11 frontend)

### Backend (JUnit 5 + Mockito) — 22 tests
```bash
cd backend
mvn test
```
Cubren las reglas de negocio de `ParkingService` (entrada con vehículo no registrado o
con estancia activa, costo de salida por tipo de vehículo, acumulación de minutos del
residente, reporte de pagos, reinicio de mes), el `QrController` (placa inválida →
400, vehículo inexistente → 404, vehículo válido → PNG) y la seguridad: `JwtService`
(firma/expiración y que **el id no viaja en claro**) y `CryptoIdService` (cifrado
AES/GCM del id).

### Frontend (Jasmine + Karma, Chrome headless) — 11 tests
```bash
cd frontend
CHROME_BIN=/ruta/al/chrome npm test
```
Cubren `VehiculoListComponent` (render, filtro por placa, lista vacía) y
`EstanciaFormComponent` (validación de placa, entrada/salida con placa en mayúsculas,
manejo de errores del backend). `karma.conf.js` ya trae el launcher
`ChromeHeadlessNoSandbox` (`--no-sandbox`) para entornos sin root.

### Verificación manual rápida (API)
```bash
curl http://localhost:8082/neo/vehiculos
```