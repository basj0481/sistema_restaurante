-- =====================================================================
-- Datos semilla minimos para poder operar el sistema desde el primer arranque.
-- Ver tambien /sql/data.sql para ejecucion manual (mismo contenido).
-- Contrasena de TODOS los usuarios semilla: Admin123!  (cumple RN05)
-- =====================================================================

-- ---------------------------------------------------------------------
-- Usuarios (CU02). El primer Administrador no lo puede crear otro
-- Administrador porque el sistema no permite autorregistro (CU00/CU02),
-- por lo que debe insertarse una unica vez por script/DBA.
-- ---------------------------------------------------------------------
INSERT INTO usuarios (nombre_completo, correo, password_hash, telefono, rol, activo, debe_cambiar_password)
VALUES
 ('Administrador General', 'admin@restaurante.com',  crypt('Admin123!', gen_salt('bf')), '00000000', 'ADMINISTRADOR', TRUE, FALSE),
 ('Mesero de Prueba',      'mesero@restaurante.com', crypt('Admin123!', gen_salt('bf')), '00000001', 'MESERO',        TRUE, FALSE),
 ('Cocina de Prueba',      'cocina@restaurante.com', crypt('Admin123!', gen_salt('bf')), '00000002', 'COCINA',        TRUE, FALSE);

-- ---------------------------------------------------------------------
-- Mesas + codigo QR (CU12 / CU13). El codigo_qr es el valor que se
-- codifica en el QR fisico impreso para cada mesa: /menu/{codigo_qr}
-- ---------------------------------------------------------------------
INSERT INTO mesas (numero, codigo_qr, activa) VALUES
 (1, 'MESA-01-QR', TRUE),
 (2, 'MESA-02-QR', TRUE),
 (3, 'MESA-03-QR', TRUE),
 (4, 'MESA-04-QR', TRUE),
 (5, 'MESA-05-QR', TRUE),
 (6, 'MESA-06-QR', TRUE),
 (7, 'MESA-07-QR', TRUE),
 (8, 'MESA-08-QR', TRUE);

-- ---------------------------------------------------------------------
-- Categorias del menu (RN02)
-- ---------------------------------------------------------------------
INSERT INTO categorias_menu (nombre, orden) VALUES
 ('Entradas', 1),
 ('Platos Fuertes', 2),
 ('Bebidas', 3),
 ('Postres', 4);

-- ---------------------------------------------------------------------
-- Insumos (CU05 / CU11)
-- ---------------------------------------------------------------------
INSERT INTO insumos (nombre, unidad_medida, stock_actual, stock_minimo, stock_bajo) VALUES
 ('Papa',            'kg',  20.000, 5.000, FALSE),
 ('Pechuga de pollo', 'kg', 15.000, 4.000, FALSE),
 ('Carne de res',     'kg', 12.000, 4.000, FALSE),
 ('Queso mozzarella', 'kg',  8.000, 2.000, FALSE),
 ('Tomate',           'kg', 10.000, 3.000, FALSE),
 ('Lechuga',          'kg',  6.000, 2.000, FALSE),
 ('Pan para hamburguesa', 'unidad', 40.000, 10.000, FALSE),
 ('Gaseosa 355ml',    'unidad', 60.000, 12.000, FALSE),
 ('Helado (base)',    'litro', 5.000, 1.000, FALSE);

-- ---------------------------------------------------------------------
-- Platillos (CU12 Ver Menu)
-- ---------------------------------------------------------------------
INSERT INTO platillos (nombre, categoria_id, precio, descripcion, estado) VALUES
 ('Papas Fritas',        (SELECT id FROM categorias_menu WHERE nombre = 'Entradas'),      25.00, 'Porcion de papas fritas con sal.', 'DISPONIBLE'),
 ('Hamburguesa Clasica',  (SELECT id FROM categorias_menu WHERE nombre = 'Platos Fuertes'), 55.00, 'Carne de res, queso, tomate y lechuga.', 'DISPONIBLE'),
 ('Pechuga a la Plancha', (SELECT id FROM categorias_menu WHERE nombre = 'Platos Fuertes'), 60.00, 'Pechuga de pollo a la plancha con guarnicion.', 'DISPONIBLE'),
 ('Gaseosa',              (SELECT id FROM categorias_menu WHERE nombre = 'Bebidas'),        12.00, 'Gaseosa 355ml.', 'DISPONIBLE'),
 ('Helado',               (SELECT id FROM categorias_menu WHERE nombre = 'Postres'),        18.00, 'Copa de helado.', 'DISPONIBLE');

-- ---------------------------------------------------------------------
-- Receta de cada platillo (RN08 - descuento automatico de inventario)
-- ---------------------------------------------------------------------
INSERT INTO receta_items (platillo_id, insumo_id, cantidad) VALUES
 ((SELECT id FROM platillos WHERE nombre = 'Papas Fritas'),
  (SELECT id FROM insumos WHERE nombre = 'Papa'), 0.300),

 ((SELECT id FROM platillos WHERE nombre = 'Hamburguesa Clasica'),
  (SELECT id FROM insumos WHERE nombre = 'Carne de res'), 0.150),
 ((SELECT id FROM platillos WHERE nombre = 'Hamburguesa Clasica'),
  (SELECT id FROM insumos WHERE nombre = 'Pan para hamburguesa'), 1.000),
 ((SELECT id FROM platillos WHERE nombre = 'Hamburguesa Clasica'),
  (SELECT id FROM insumos WHERE nombre = 'Queso mozzarella'), 0.050),
 ((SELECT id FROM platillos WHERE nombre = 'Hamburguesa Clasica'),
  (SELECT id FROM insumos WHERE nombre = 'Tomate'), 0.030),
 ((SELECT id FROM platillos WHERE nombre = 'Hamburguesa Clasica'),
  (SELECT id FROM insumos WHERE nombre = 'Lechuga'), 0.020),

 ((SELECT id FROM platillos WHERE nombre = 'Pechuga a la Plancha'),
  (SELECT id FROM insumos WHERE nombre = 'Pechuga de pollo'), 0.250),

 ((SELECT id FROM platillos WHERE nombre = 'Gaseosa'),
  (SELECT id FROM insumos WHERE nombre = 'Gaseosa 355ml'), 1.000),

 ((SELECT id FROM platillos WHERE nombre = 'Helado'),
  (SELECT id FROM insumos WHERE nombre = 'Helado (base)'), 0.150);
