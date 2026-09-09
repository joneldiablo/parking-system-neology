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

## API REST (`/neo`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/neo/vehiculos/oficiales` | Alta de vehículo oficial (no paga) |
| POST | `/neo/vehiculos/residentes` | Alta de vehículo residente |
| POST | `/neo/vehiculos/no-residentes` | Alta de vehículo no residente |
| POST | `/neo/estancias/entrada` | Registrar entrada de vehículo |
| POST | `/neo/estancias/salida` | Registrar salida (calcula cobro) |
| GET | `/neo/residentes/pagos` | Informe de pagos de residentes |
| POST | `/neo/mes/iniciar` | Reiniciar mes (re)establecer estancias y residentes |
| GET | `/neo/vehiculos` | Listar todos los vehículos |
| GET | `/neo/estancias` | Listar todas las estancias |
| GET | `/neo/estancias/{placa}` | Estancias de un vehículo |

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

## Pruebas

### Backend (JUnit + Mockito, Spring Boot Test con H2)
```bash
cd backend
mvn test
```

### Frontend (Jasmine + Karma, Chrome headless)
```bash
cd frontend
npm test
```

### Verificación manual rápida (API)
```bash
curl http://localhost:8082/neo/vehiculos
```