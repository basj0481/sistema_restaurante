# Scripts SQL — Sistema de Gestión de Restaurante

Estos scripts crean y llenan la base de datos **PostgreSQL** manualmente.
Ejecútalos **en orden**.

> Nota: el backend (Spring Boot + Flyway) también puede crear y migrar el
> esquema automáticamente al arrancar (ver `backend/src/main/resources/db/migration`,
> que contiene exactamente el mismo contenido que `02_schema.sql` y `03_data.sql`).
> Usa esta carpeta si prefieres ejecutar el SQL tú mismo, por ejemplo la
> primera vez, o para inspeccionar/adaptar el modelo antes de correr la app.

## Orden de ejecución

```bash
# 1. Crear el usuario y la base de datos (conectado como superusuario, ej. "postgres")
psql -U postgres -f 01_create_database.sql

# 2. Crear el esquema (tablas, llaves foráneas, índices)
psql -U restaurante_user -d restaurante_db -f 02_schema.sql

# 3. Cargar datos semilla (usuarios de prueba, mesas con QR, menú, insumos)
psql -U restaurante_user -d restaurante_db -f 03_data.sql
```

Si prefieres hacerlo todo en un solo comando:

```bash
psql -U postgres -f 01_create_database.sql \
  && psql -U restaurante_user -d restaurante_db -f 02_schema.sql \
  && psql -U restaurante_user -d restaurante_db -f 03_data.sql
```

## Usuarios de prueba creados por `03_data.sql`

Todos con la contraseña **`Admin123!`** (cumple RN05: 8+ caracteres, mayúscula, número y carácter especial).

| Correo                     | Rol            |
|-----------------------------|----------------|
| admin@restaurante.com       | ADMINISTRADOR  |
| mesero@restaurante.com      | MESERO         |
| cocina@restaurante.com      | COCINA         |

## Mesas y códigos QR

Se crean 8 mesas (`01_schema.sql` → tabla `mesas`) con un `codigo_qr` de
ejemplo (`MESA-01-QR` … `MESA-08-QR`). En producción, cada código se
codifica en un QR físico que apunta a:

```
http://<dominio-del-frontend>/menu/MESA-01-QR
```

El frontend público (CU12/CU13) lee ese código de la URL y lo envía al
backend (`GET /api/publico/mesas/{codigoQr}/menu`).

## Reiniciar desde cero

```sql
DROP DATABASE restaurante_db;
DROP USER restaurante_user;
```

y vuelve a correr los 3 pasos.
