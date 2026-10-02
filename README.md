# sistema_restaurante

agregar esto a la bd (restaurante_bd) para darle permisos: 

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Permitir que restaurante_user utilice el esquema public

GRANT USAGE, CREATE ON SCHEMA public TO restaurante_user;

-- Dar permisos completos sobre las tablas existentes

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public
TO restaurante_user;

-- Dar permisos sobre las secuencias

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public
TO restaurante_user;

-- Cambiar el propietario de las tablas existentes a restaurante_user

DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
          AND tableowner = 'postgres'
    LOOP
        EXECUTE format(
            'ALTER TABLE public.%I OWNER TO restaurante_user',
            r.tablename
        );
    END LOOP;
END
$$;

-- Cambiar el propietario de las secuencias existentes
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT sequence_name
        FROM information_schema.sequences
        WHERE sequence_schema = 'public'
    LOOP
        EXECUTE format(
            'ALTER SEQUENCE public.%I OWNER TO restaurante_user',
            r.sequence_name
        );
    END LOOP;
END
$$;

SELECT
    schemaname,
    tablename,
    tableowner
FROM pg_tables
WHERE tablename = 'usuarios';
