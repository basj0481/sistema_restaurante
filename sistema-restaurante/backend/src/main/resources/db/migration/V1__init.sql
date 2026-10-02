-- =====================================================================
-- Sistema de Gestion de Restaurante - Esquema inicial (V1)
-- Ejecutado automaticamente por Flyway al arrancar el backend.
-- Ver tambien /sql/schema.sql para ejecucion manual (mismo contenido).
-- =====================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ---------------------------------------------------------------------
-- usuarios (CU02 Registrar Usuario / RN01 Roles)
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id                      BIGSERIAL PRIMARY KEY,
    nombre_completo         VARCHAR(150) NOT NULL,
    correo                  VARCHAR(150) NOT NULL UNIQUE,
    password_hash           VARCHAR(200) NOT NULL,
    telefono                VARCHAR(30),
    rol                     VARCHAR(20)  NOT NULL CHECK (rol IN ('ADMINISTRADOR','MESERO','COCINA')),
    activo                  BOOLEAN      NOT NULL DEFAULT TRUE,
    debe_cambiar_password   BOOLEAN      NOT NULL DEFAULT TRUE,
    intentos_fallidos       INTEGER      NOT NULL DEFAULT 0,
    fecha_creacion          TIMESTAMP    NOT NULL DEFAULT now(),
    creado_por              BIGINT,
    -- CU02 campo g. Horario de trabajo (usado por CU01 FA04 para el cierre de sesion automatico)
    hora_inicio_trabajo     TIME,
    hora_fin_trabajo        TIME,
    token_reset             VARCHAR(100),
    token_reset_expira      TIMESTAMP
);

-- ---------------------------------------------------------------------
-- planilla (CU02 campo h. Salario, en su propia tabla)
-- ---------------------------------------------------------------------
CREATE TABLE planilla (
    id                      BIGSERIAL PRIMARY KEY,
    usuario_id              BIGINT        NOT NULL UNIQUE REFERENCES usuarios(id),
    salario                 NUMERIC(10,2) NOT NULL CHECK (salario >= 0),
    fecha_actualizacion     TIMESTAMP     NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- mesas (CU12 Ver Menu / CU13 Llamar al Mesero - identificadas por QR)
-- ---------------------------------------------------------------------
CREATE TABLE mesas (
    id          BIGSERIAL PRIMARY KEY,
    numero      INTEGER     NOT NULL UNIQUE,
    codigo_qr   VARCHAR(60) NOT NULL UNIQUE,
    activa      BOOLEAN     NOT NULL DEFAULT TRUE
);

-- ---------------------------------------------------------------------
-- categorias_menu (RN02)
-- ---------------------------------------------------------------------
CREATE TABLE categorias_menu (
    id      BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(80) NOT NULL UNIQUE,
    orden   INTEGER     NOT NULL DEFAULT 0
);

-- ---------------------------------------------------------------------
-- platillos (CU12 Ver Menu)
-- ---------------------------------------------------------------------
CREATE TABLE platillos (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(120)  NOT NULL,
    categoria_id    BIGINT        NOT NULL REFERENCES categorias_menu(id),
    precio          NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    descripcion     VARCHAR(500),
    foto_url        VARCHAR(300),
    estado          VARCHAR(20)   NOT NULL DEFAULT 'DISPONIBLE' CHECK (estado IN ('DISPONIBLE','AGOTADO'))
);

-- ---------------------------------------------------------------------
-- insumos (CU05 Consultar Inventario / CU11 Agregar Productos al Inventario)
-- ---------------------------------------------------------------------
CREATE TABLE insumos (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(120)   NOT NULL UNIQUE,
    unidad_medida   VARCHAR(20)    NOT NULL,
    stock_actual    NUMERIC(12,3)  NOT NULL DEFAULT 0,
    stock_minimo    NUMERIC(12,3)  NOT NULL DEFAULT 0,
    stock_bajo      BOOLEAN        NOT NULL DEFAULT FALSE
);

-- ---------------------------------------------------------------------
-- receta_items (RN08 - insumos y cantidad que se descuentan por platillo)
-- ---------------------------------------------------------------------
CREATE TABLE receta_items (
    id          BIGSERIAL PRIMARY KEY,
    platillo_id BIGINT        NOT NULL REFERENCES platillos(id) ON DELETE CASCADE,
    insumo_id   BIGINT        NOT NULL REFERENCES insumos(id),
    cantidad    NUMERIC(12,3) NOT NULL CHECK (cantidad > 0)
);

-- ---------------------------------------------------------------------
-- pedidos (CU07 Generar Pedido / CU09 / CU10 / CU08 Cobrar Cuenta)
-- ---------------------------------------------------------------------
CREATE TABLE pedidos (
    id                  BIGSERIAL PRIMARY KEY,
    tipo                VARCHAR(20)   NOT NULL CHECK (tipo IN ('MESA','PARA_LLEVAR')),
    mesa_id             BIGINT        REFERENCES mesas(id),
    cliente_nombre      VARCHAR(120),
    cliente_telefono    VARCHAR(30),
    mesero_id           BIGINT        NOT NULL REFERENCES usuarios(id),
    estado              VARCHAR(20)   NOT NULL DEFAULT 'ENVIADO'
                          CHECK (estado IN ('ENVIADO','EN_PREPARACION','LISTO','ENTREGADO','COBRADO','CANCELADO')),
    subtotal            NUMERIC(10,2) NOT NULL DEFAULT 0,
    total               NUMERIC(10,2) NOT NULL DEFAULT 0,
    motivo_cancelacion  VARCHAR(300),
    fecha_creacion      TIMESTAMP     NOT NULL DEFAULT now(),
    fecha_recibido      TIMESTAMP,
    fecha_listo         TIMESTAMP,
    fecha_cobrado       TIMESTAMP
);

-- ---------------------------------------------------------------------
-- pedido_items
-- ---------------------------------------------------------------------
CREATE TABLE pedido_items (
    id                BIGSERIAL PRIMARY KEY,
    pedido_id         BIGINT        NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    platillo_id       BIGINT        NOT NULL REFERENCES platillos(id),
    cantidad          INTEGER       NOT NULL CHECK (cantidad > 0),
    precio_unitario   NUMERIC(10,2) NOT NULL,
    notas             VARCHAR(200),
    cancelado         BOOLEAN       NOT NULL DEFAULT FALSE
);

-- ---------------------------------------------------------------------
-- movimientos_inventario (CU11 / RN08 descuento automatico y restitucion)
-- ---------------------------------------------------------------------
CREATE TABLE movimientos_inventario (
    id          BIGSERIAL PRIMARY KEY,
    insumo_id   BIGINT        NOT NULL REFERENCES insumos(id),
    tipo        VARCHAR(30)   NOT NULL
                  CHECK (tipo IN ('ENTRADA','CREACION_INSUMO','DESCUENTO_PEDIDO','RESTITUCION_CANCELACION')),
    cantidad    NUMERIC(12,3) NOT NULL,
    usuario_id  BIGINT        REFERENCES usuarios(id),
    pedido_id   BIGINT        REFERENCES pedidos(id),
    motivo      VARCHAR(300),
    fecha       TIMESTAMP     NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- pagos (CU08 Cobrar Cuenta)
-- ---------------------------------------------------------------------
CREATE TABLE pagos (
    id                   BIGSERIAL PRIMARY KEY,
    pedido_id            BIGINT        NOT NULL UNIQUE REFERENCES pedidos(id),
    mesero_id            BIGINT        NOT NULL REFERENCES usuarios(id),
    metodo_pago          VARCHAR(20)   NOT NULL CHECK (metodo_pago IN ('EFECTIVO','TARJETA')),
    total                NUMERIC(10,2) NOT NULL,
    monto_recibido       NUMERIC(10,2),
    cambio               NUMERIC(10,2),
    numero_comprobante   VARCHAR(30)   NOT NULL UNIQUE,
    -- CU08: comprobante asociado con la API de la SAT (ver SatFacturacionService)
    serie_sat            VARCHAR(10),
    numero_autorizacion_sat VARCHAR(60),
    fecha                TIMESTAMP     NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- llamados_mesero (CU13 Llamar al Mesero)
-- ---------------------------------------------------------------------
CREATE TABLE llamados_mesero (
    id              BIGSERIAL PRIMARY KEY,
    mesa_id         BIGINT      NOT NULL REFERENCES mesas(id),
    estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE','ATENDIDO')),
    fecha_creacion  TIMESTAMP   NOT NULL DEFAULT now(),
    fecha_atendido  TIMESTAMP,
    atendido_por    BIGINT      REFERENCES usuarios(id)
);

-- ---------------------------------------------------------------------
-- bitacora (CU04 Consultar Bitacora del Sistema / RN06 - nunca se edita ni se borra)
-- ---------------------------------------------------------------------
CREATE TABLE bitacora (
    id          BIGSERIAL PRIMARY KEY,
    tipo        VARCHAR(20)  NOT NULL CHECK (tipo IN ('TRANSACCION','USUARIO')),
    usuario_id  BIGINT       REFERENCES usuarios(id),
    accion      VARCHAR(80)  NOT NULL,
    detalle     VARCHAR(500),
    fecha       TIMESTAMP    NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- Indices de apoyo para las consultas mas frecuentes
-- ---------------------------------------------------------------------
CREATE INDEX idx_pedidos_estado            ON pedidos(estado);
CREATE INDEX idx_pedidos_mesero            ON pedidos(mesero_id);
CREATE INDEX idx_pedidos_fecha_cobrado     ON pedidos(fecha_cobrado);
CREATE INDEX idx_movimientos_insumo        ON movimientos_inventario(insumo_id);
CREATE INDEX idx_bitacora_tipo_fecha       ON bitacora(tipo, fecha);
CREATE INDEX idx_bitacora_usuario          ON bitacora(usuario_id);
CREATE INDEX idx_llamados_estado           ON llamados_mesero(estado);
