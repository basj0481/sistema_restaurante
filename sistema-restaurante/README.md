# Sistema de Gestión de Restaurante

Implementación del sistema descrito en los 14 casos de uso (CU00–CU13) y las
Reglas de Negocio (RN01–RN10). Roles operativos: **Administrador**, **Mesero**,
**Cocina**; el **Cliente** no tiene cuenta y accede escaneando el código QR de
su mesa (CU12 Ver Menú, CU13 Llamar al Mesero).

## Stack

| Capa      | Tecnología |
|-----------|------------|
| Frontend  | Angular 18 (standalone components), TypeScript |
| Backend   | Java 21, Spring Boot 3.3 (Web, Security, Data JPA, Validation, Mail), JWT |
| Base de datos | PostgreSQL 16 (local, vía Docker o instalación nativa) |
| Migraciones | Flyway (automáticas al arrancar el backend) |

## Estructura del proyecto

```
sistema-restaurante/
├── backend/     Spring Boot (Java 21) — API REST
├── frontend/    Angular 18 — SPA
├── sql/         Scripts SQL para ejecución MANUAL (alternativa a Flyway)
└── docker-compose.yml   Levanta solo PostgreSQL
```

## 1. Base de datos

**Opción A — Docker (recomendada):**
```bash
docker compose up -d
```
Esto crea el usuario/BD (`restaurante_user` / `restaurante_db`) automáticamente
vía variables de entorno. El **esquema y los datos semilla** los aplica Flyway
al arrancar el backend (ver paso 2) — no hace falta correr nada de `/sql`.

**Opción B — PostgreSQL instalado localmente + SQL manual:**
```bash
cd sql
psql -U postgres -f 01_create_database.sql
psql -U restaurante_user -d restaurante_db -f 02_schema.sql
psql -U restaurante_user -d restaurante_db -f 03_data.sql
```
Ver `sql/README.md` para más detalle. Si usas esta opción, en
`backend/src/main/resources/application.yml` deja `spring.flyway.enabled: true`
igualmente — Flyway detectará que el esquema ya existe (usa `baseline-on-migrate`)
y no lo tocará dos veces siempre que la versión coincida; si prefieres que NO
vuelva a intentar migrar, cámbialo a `false`.

## 2. Backend (Spring Boot)

Requiere **JDK 21** y **Maven** (o usa `./mvnw` si lo agregas con
`mvn -N io.takari:maven:wrapper`).

```bash
cd backend
mvn spring-boot:run
```

La API queda en `http://localhost:8080/api`. Swagger/OpenAPI en
`http://localhost:8080/docs`.

Variables de entorno relevantes (ver `application.yml`):
- `JWT_SECRET` — clave HMAC para firmar los tokens (cámbiala en producción).
- `MAIL_USERNAME` / `MAIL_PASSWORD` — para CU03 Recuperar Contraseña. Si no
  se configuran, el envío de correo se degrada a un `log` en consola (no
  bloquea el flujo funcional, ver `EmailService`).
- `FRONTEND_URL` — usada para armar el enlace de recuperación de contraseña.

## 3. Frontend (Angular)

Requiere **Node.js 18+**.

```bash
cd frontend
npm install
npm start
```

Abre `http://localhost:4200`. El frontend consume la API en
`http://localhost:8080/api` (ver `src/environments/environment.ts`).

## 4. Usuarios de prueba (creados por el seed)

Contraseña para todos: **`Admin123!`**

| Correo | Rol | Horario | Salario (planilla) |
|---|---|---|---|
| admin@restaurante.com | ADMINISTRADOR | 00:00–23:59 (sin restricción práctica) | Q8,000.00 |
| mesero@restaurante.com | MESERO | 07:00–19:00 | Q2,800.00 |
| cocina@restaurante.com | COCINA | 06:00–18:00 | Q3,200.00 |

**CU01 FA04 — cierre de sesión por horario:** fuera del rango configurado
para cada usuario, el backend deja de autenticar sus peticiones (401) y el
frontend cierra la sesión automáticamente. Si necesitas probar esto fuera
de ese rango, edita el horario del usuario en el módulo "Usuarios" o
directamente en la tabla `usuarios`.

**CU02 — Salario:** se guarda en su propia tabla, `planilla` (1:1 con `usuarios`).

## 5. Menú digital del Cliente (CU12 / CU13)

El menú que ve el Cliente es un **PDF** que el Administrador sube desde el
módulo "Menú" (`/admin/menu`, sección "Menú en PDF"). Hasta que no se suba
un PDF, la pantalla pública mostrará "El menú aún no está disponible".

Sin backend corriendo no hay datos, pero una vez arriba (y con un PDF
subido), cada mesa sembrada tiene un código QR de prueba: visita, por
ejemplo:

```
http://localhost:4200/menu/MESA-01-QR
```

para ver el menú en PDF y probar "Llamar al Mesero" tal como lo haría un
cliente que escanea el QR físico de la mesa 1.

Nota: los platillos/categorías (CRUD en el mismo módulo "Menú") se
mantienen — el Mesero los sigue usando para armar pedidos en CU07; solo la
vista del Cliente cambió de tarjetas a PDF.

## 6. Comprobante y API de la SAT (CU08)

Al cobrar una cuenta, el comprobante incluye `serieSat` y
`numeroAutorizacionSat`. **Esto está simulado** (`SatFacturacionServiceSimulado`
genera un UUID) — no hay integración real con la SAT de Guatemala. Para
producción, sustituye esa clase por una implementación de
`SatFacturacionService` que llame a un certificador FEL autorizado.

## 7. Mapeo Caso de Uso → Endpoint

| CU | Endpoint(s) |
|---|---|
| CU00/CU01 Portal / Iniciar Sesión | `POST /api/auth/login` |
| CU02 Registrar Usuario | `POST /api/usuarios` (+ `GET`, `PATCH /{id}/estado`) |
| CU03 Recuperar Contraseña | `POST /api/auth/forgot-password`, `POST /api/auth/reset-password` |
| CU04 Consultar Bitácora | `GET /api/bitacora` |
| CU05 Consultar Inventario | `GET /api/inventario` |
| CU06 Reporte de Ventas | `GET /api/reportes/ventas` |
| CU07 Generar Pedido | `POST /api/pedidos` |
| CU08 Cobrar Cuenta | `POST /api/pedidos/{id}/cobrar` |
| CU09 Recibir Pedido | `PATCH /api/cocina/pedidos/{id}/recibir` |
| CU10 Marcar Pedido Listo | `PATCH /api/cocina/pedidos/{id}/listo` |
| CU11 Agregar Productos al Inventario | `POST /api/inventario/entrada`, `POST /api/inventario` |
| CU12 Ver Menú (QR, en PDF) | `GET /api/publico/mesas/{codigoQr}/menu`, `GET /api/publico/menu.pdf`, admin: `POST/GET /api/menu/pdf` |
| CU13 Llamar al Mesero (QR) | `POST /api/publico/mesas/{codigoQr}/llamar-mesero`, `GET/PATCH /api/llamados` |

## 8. Notas de diseño y alcance

- **Tiempo real**: en vez de WebSockets, el frontend usa sondeo periódico
  (polling cada 4–5s) en Cocina, Pedidos del Mesero y Llamados, para mantener
  el proyecto simple de levantar. Es un punto sencillo de evolucionar a
  WebSocket/STOMP si se requiere verdadero push.
- **Gestión del menú**: ningún CU del alcance actual cubre quién crea o edita
  los platillos que el Cliente ve en CU12. Se agregó un módulo mínimo de
  mantenimiento para el Administrador (`/api/menu/**`, pantalla "Menú") para
  que el sistema sea utilizable de punta a punta. Avísame si prefieres que
  se formalice como un CU aparte.
- **Pago con tarjeta (CU08 FA02)**: se asume aprobación inmediata de una
  terminal de pago simulada; no hay integración real con una pasarela.
- **RN08 descuento automático de inventario**: al enviar un pedido (CU07) se
  descuenta el stock según la receta de cada platillo; al cancelar un pedido
  se restituye automáticamente.
