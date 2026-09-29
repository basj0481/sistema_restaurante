-- =====================================================================
-- Sistema de Gestion de Restaurante
-- Paso 1: crear la base de datos y el usuario de aplicacion.
--
-- Ejecutar este archivo conectado como superusuario (por ejemplo "postgres"):
--   psql -U postgres -f 01_create_database.sql
--
-- Los datos de conexion aqui definidos deben coincidir con
-- backend/src/main/resources/application.yml (spring.datasource.*)
-- =====================================================================

CREATE USER restaurante_user WITH PASSWORD 'restaurante_pass';

CREATE DATABASE restaurante_db
    OWNER restaurante_user
    ENCODING 'UTF8';

GRANT ALL PRIVILEGES ON DATABASE restaurante_db TO restaurante_user;
