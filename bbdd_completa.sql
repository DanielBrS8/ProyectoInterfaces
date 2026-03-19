-- ==============================================================================
-- LIMPIEZA PREVIA
-- ==============================================================================
DROP TABLE IF EXISTS archivos_adjuntos CASCADE;
DROP TABLE IF EXISTS mensajes CASCADE;
DROP TABLE IF EXISTS consultas_chat CASCADE;
DROP TABLE IF EXISTS acciones_tamagochi CASCADE;
DROP TABLE IF EXISTS donaciones CASCADE;
DROP TABLE IF EXISTS foro_respuestas CASCADE;
DROP TABLE IF EXISTS valoraciones CASCADE;
DROP TABLE IF EXISTS mascotas_virtuales CASCADE;
DROP TABLE IF EXISTS campañas_donacion CASCADE;
DROP TABLE IF EXISTS foro_hilos CASCADE;
DROP TABLE IF EXISTS alquiler CASCADE;
DROP TABLE IF EXISTS paseos CASCADE;
DROP TABLE IF EXISTS vacunas CASCADE;
DROP TABLE IF EXISTS inventario CASCADE;
DROP TABLE IF EXISTS logros_usuario CASCADE;
DROP TABLE IF EXISTS reseñas_centros CASCADE;
DROP TABLE IF EXISTS notificaciones CASCADE;
DROP TABLE IF EXISTS mascotas CASCADE;
DROP TABLE IF EXISTS tienda_virtual CASCADE;
DROP TABLE IF EXISTS logros CASCADE;
DROP TABLE IF EXISTS foro_categorias CASCADE;
DROP TABLE IF EXISTS centros_veterinarios CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- ==============================================================================
-- NIVEL 0: Tablas sin dependencias
-- ==============================================================================

CREATE TABLE usuarios (
    id_usuario      SERIAL PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    email           VARCHAR(150) UNIQUE NOT NULL,
    password        VARCHAR(255),                           
    google_id       VARCHAR(255) UNIQUE,                    
    foto_perfil     VARCHAR(255),                           
    telefono        VARCHAR(20),
    direccion       VARCHAR(255),
    rol             VARCHAR(20)  DEFAULT 'user',            
    activo          INT          DEFAULT 1,
    monedas         INT          DEFAULT 0,
    fecha_registro  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE centros_veterinarios (
    id_centro    SERIAL PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    ciudad       VARCHAR(100),
    direccion    VARCHAR(255),
    telefono     VARCHAR(20),
    especialidad VARCHAR(100),
    foto         VARCHAR(255),
    horario      TEXT,
    latitud      NUMERIC(10, 8),
    longitud     NUMERIC(11, 8)
);

CREATE TABLE foro_categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  TEXT,
    icono        VARCHAR(50)
);

CREATE TABLE logros (
    id_logro          SERIAL PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    descripcion       TEXT,
    icono             VARCHAR(255),
    puntos_necesarios INT DEFAULT 0
);

CREATE TABLE tienda_virtual (
    id_item          SERIAL PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    tipo             VARCHAR(50),                           
    coste_monedas    INT NOT NULL CHECK (coste_monedas > 0),
    efecto_hambre    INT DEFAULT 0,
    efecto_felicidad INT DEFAULT 0,
    efecto_energia   INT DEFAULT 0,
    efecto_higiene   INT DEFAULT 0
);

-- ==============================================================================
-- NIVEL 1: Tablas con dependencia directa de Nivel 0
-- ==============================================================================

CREATE TABLE mascotas (
    id_mascota         SERIAL PRIMARY KEY,
    id_centro          INT REFERENCES centros_veterinarios(id_centro) ON DELETE SET NULL,
    nombre             VARCHAR(100) NOT NULL,
    especie            VARCHAR(50)  NOT NULL,
    raza               VARCHAR(100),
    fecha_nacimiento   DATE,
    peso               NUMERIC(5, 2) CHECK (peso > 0),
    estado_salud       VARCHAR(100),
    disponible_alquiler INT DEFAULT 1,
    foto               VARCHAR(255),
    notas              TEXT,
    fecha_creacion     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notificaciones (
    id_notificacion SERIAL PRIMARY KEY,
    id_usuario      INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    tipo            VARCHAR(50),                            
    contenido       TEXT NOT NULL,
    leida           BOOLEAN   DEFAULT FALSE,
    fecha           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reseñas_centros (
    id_reseña   SERIAL PRIMARY KEY,
    id_centro   INT REFERENCES centros_veterinarios(id_centro) ON DELETE CASCADE,
    id_usuario  INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    puntuacion  INT CHECK (puntuacion >= 1 AND puntuacion <= 5),
    comentario  TEXT,
    fecha       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_centro, id_usuario)                         
);

CREATE TABLE logros_usuario (
    id               SERIAL PRIMARY KEY,
    id_usuario       INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_logro         INT REFERENCES logros(id_logro)    ON DELETE CASCADE,
    fecha_obtencion  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_usuario, id_logro)
);

CREATE TABLE inventario (
    id        SERIAL PRIMARY KEY,
    id_usuario INT REFERENCES usuarios(id_usuario)      ON DELETE CASCADE,
    id_item    INT REFERENCES tienda_virtual(id_item)   ON DELETE CASCADE,
    cantidad   INT DEFAULT 0 CHECK (cantidad >= 0),
    UNIQUE (id_usuario, id_item)
);

-- ==============================================================================
-- NIVEL 2: Tablas de Gestión y Actividad
-- ==============================================================================

CREATE TABLE vacunas (
    id_vacuna    SERIAL PRIMARY KEY,
    id_mascota   INT REFERENCES mascotas(id_mascota) ON DELETE CASCADE,
    nombre       VARCHAR(100) NOT NULL,
    fecha        DATE NOT NULL,
    proxima_dosis DATE,
    notas        TEXT
);

CREATE TABLE paseos (
    id_paseo   SERIAL PRIMARY KEY,
    id_mascota INT REFERENCES mascotas(id_mascota) ON DELETE CASCADE,
    id_usuario INT REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    fecha      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duracion   INT CHECK (duracion > 0),                   
    distancia  NUMERIC(5, 2) CHECK (distancia >= 0),       
    ruta       JSONB                                        
);

CREATE TABLE alquiler (
    id_alquiler  SERIAL PRIMARY KEY,
    id_mascota   INT REFERENCES mascotas(id_mascota)  ON DELETE CASCADE,
    id_voluntario INT REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin    DATE,                                      
    estado       VARCHAR(50) DEFAULT 'pendiente'            
);

CREATE TABLE foro_hilos (
    id_hilo      SERIAL PRIMARY KEY,
    id_categoria INT REFERENCES foro_categorias(id_categoria) ON DELETE CASCADE,
    id_usuario   INT REFERENCES usuarios(id_usuario)          ON DELETE SET NULL,
    titulo       VARCHAR(255) NOT NULL,
    contenido    TEXT NOT NULL,
    fecha        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vistas       INT DEFAULT 0 CHECK (vistas >= 0)
);

CREATE TABLE campañas_donacion (
    id_campaña      SERIAL PRIMARY KEY,
    id_mascota      INT REFERENCES mascotas(id_mascota) ON DELETE SET NULL,
    titulo          VARCHAR(255)    NOT NULL,
    descripcion     TEXT,
    objetivo_dinero NUMERIC(10, 2)  NOT NULL CHECK (objetivo_dinero > 0),
    estado          VARCHAR(50)     DEFAULT 'abierta',      
    fecha_inicio    DATE            DEFAULT CURRENT_DATE,
    fecha_fin       DATE
);

CREATE TABLE mascotas_virtuales (
    id_virtual           SERIAL PRIMARY KEY,
    id_usuario           INT REFERENCES usuarios(id_usuario)  ON DELETE CASCADE,
    id_mascota_real      INT REFERENCES mascotas(id_mascota)  ON DELETE SET NULL,
    nombre               VARCHAR(100) NOT NULL,
    especie              VARCHAR(50),
    nivel                INT DEFAULT 1  CHECK (nivel >= 1),
    experiencia          INT DEFAULT 0  CHECK (experiencia >= 0),
    hambre               INT DEFAULT 100 CHECK (hambre    BETWEEN 0 AND 100),
    felicidad            INT DEFAULT 100 CHECK (felicidad BETWEEN 0 AND 100),
    energia              INT DEFAULT 100 CHECK (energia   BETWEEN 0 AND 100),
    higiene              INT DEFAULT 100 CHECK (higiene   BETWEEN 0 AND 100),
    ultima_interaccion   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- NIVEL 3: Tablas de Interacción Profunda
-- ==============================================================================

CREATE TABLE valoraciones (
    id_valoracion SERIAL PRIMARY KEY,
    id_alquiler   INT REFERENCES alquiler(id_alquiler) ON DELETE CASCADE,
    id_usuario    INT REFERENCES usuarios(id_usuario)  ON DELETE CASCADE,
    puntuacion    INT CHECK (puntuacion >= 1 AND puntuacion <= 5),
    comentario    TEXT,
    fecha         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id_alquiler, id_usuario)                        
);

CREATE TABLE consultas_chat (
    id_conversacion SERIAL PRIMARY KEY,
    id_mascota      INT REFERENCES mascotas(id_mascota)  ON DELETE CASCADE,
    id_usuario      INT REFERENCES usuarios(id_usuario)  ON DELETE CASCADE,
    id_veterinario  INT REFERENCES usuarios(id_usuario)  ON DELETE SET NULL,
    id_alquiler     INT REFERENCES alquiler(id_alquiler) ON DELETE SET NULL,
    asunto          VARCHAR(255),
    fecha_inicio    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado          VARCHAR(50) DEFAULT 'activa'            
);

CREATE TABLE foro_respuestas (
    id_respuesta SERIAL PRIMARY KEY,
    id_hilo      INT REFERENCES foro_hilos(id_hilo)    ON DELETE CASCADE,
    id_usuario   INT REFERENCES usuarios(id_usuario)   ON DELETE SET NULL,
    contenido    TEXT NOT NULL,
    fecha        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    votos        INT DEFAULT 0
);

CREATE TABLE donaciones (
    id_donacion SERIAL PRIMARY KEY,
    id_campaña  INT REFERENCES campañas_donacion(id_campaña) ON DELETE CASCADE,
    id_usuario  INT REFERENCES usuarios(id_usuario)          ON DELETE SET NULL,
    cantidad    NUMERIC(10, 2) NOT NULL CHECK (cantidad > 0),
    fecha       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE acciones_tamagochi (
    id_accion          SERIAL PRIMARY KEY,
    id_mascota_virtual INT REFERENCES mascotas_virtuales(id_virtual) ON DELETE CASCADE,
    tipo               VARCHAR(50),                         
    puntos_ganados     INT DEFAULT 0 CHECK (puntos_ganados >= 0),
    fecha              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==============================================================================
-- NIVEL 4 y 5: Mensajería
-- ==============================================================================

CREATE TABLE mensajes (
    id_mensaje      SERIAL PRIMARY KEY,
    id_conversacion INT REFERENCES consultas_chat(id_conversacion) ON DELETE CASCADE,
    id_emisor       INT REFERENCES usuarios(id_usuario)            ON DELETE CASCADE,
    contenido       TEXT NOT NULL,
    fecha           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leido           BOOLEAN DEFAULT FALSE
);

CREATE TABLE archivos_adjuntos (
    id_adjunto  SERIAL PRIMARY KEY,
    id_mensaje  INT REFERENCES mensajes(id_mensaje) ON DELETE CASCADE,
    url_archivo VARCHAR(255) NOT NULL,
    tipo        VARCHAR(50)                                 
);





-- DATOS DE PRUEBA






-- ==============================================================================
-- NIVEL 0: Datos base (Sin dependencias)
-- ==============================================================================

INSERT INTO usuarios (nombre, email, password, telefono, direccion, rol, activo, monedas) VALUES
('Carlos Admin', 'admin@pawlink.com', 'admin', '600111222', 'Calle Principal 1', 'admin', 1, 1000),
('Laura Veterinaria', 'vet@pawlink.com', 'vet123', '600333444', 'Avenida Clinica 4', 'veterinario', 1, 500),
('Miguel Voluntario', 'voluntario@pawlink.com', 'vol123', '600555666', 'Plaza Refugio 2', 'voluntario', 1, 250),
('Ana Dueña', 'ana@gmail.com', 'ana123', '600777888', 'Calle Parque 9', 'user', 1, 50),
('Google User', 'google@gmail.com', NULL, NULL, 'Calle Nube 3', 'user', 1, 100);

INSERT INTO centros_veterinarios (nombre, ciudad, direccion, telefono, especialidad, horario, latitud, longitud) VALUES
('Clínica PawLink Central', 'Madrid', 'Calle de los Animales 12', '910000001', 'General y Cirugía', 'L-V 09:00-20:00', 40.416775, -3.703790),
('Refugio y Salud Sur', 'Granada', 'Avenida del Sur 45', '958000002', 'Adopciones y Rehabilitación', 'L-D 10:00-18:00', 37.177336, -3.598557);

INSERT INTO foro_categorias (nombre, descripcion, icono) VALUES
('Salud y Nutrición', 'Dudas sobre alimentación y bienestar', 'health_icon'),
('Adopciones', 'Experiencias y consejos para nuevos adoptantes', 'home_icon'),
('Comportamiento', 'Educación y adiestramiento', 'brain_icon');

INSERT INTO logros (nombre, descripcion, icono, puntos_necesarios) VALUES
('Primeros Pasos', 'Adopta tu primera mascota virtual', 'egg_icon', 0),
('Cuidador Experto', 'Mantén la felicidad al 100% durante 3 días', 'star_icon', 500),
('Paseador Incansable', 'Realiza 10 paseos virtuales', 'shoe_icon', 300);

INSERT INTO tienda_virtual (nombre, tipo, coste_monedas, efecto_hambre, efecto_felicidad, efecto_energia, efecto_higiene) VALUES
('Pienso Premium', 'comida', 20, 40, 5, 10, -5),
('Pelota de Tenis', 'juguete', 50, -5, 30, -10, -15),
('Cepillo Suave', 'accesorio', 30, 0, 15, 0, 40);

-- ==============================================================================
-- NIVEL 1: Dependencias directas de Nivel 0
-- ==============================================================================

INSERT INTO mascotas (id_centro, nombre, especie, raza, fecha_nacimiento, peso, estado_salud, disponible_alquiler, notas) VALUES
(1, 'Max', 'Perro', 'Golden Retriever', '2021-05-10', 25.5, 'Sano', 1, 'Muy amigable, ideal para familias.'),
(1, 'Luna', 'Gato', 'Siamés', '2022-08-15', 4.2, 'En tratamiento leve', 1, 'Tímida al principio, requiere paciencia.'),
(2, 'Rocky', 'Perro', 'Mestizo', '2023-01-20', 15.0, 'Sano', 0, 'Actualmente en proceso de adopción.'),
(2, 'Kiwi', 'Pájaro', 'Canario', '2023-11-05', 0.1, 'Sano', 1, 'Canta por las mañanas.');

INSERT INTO notificaciones (id_usuario, tipo, contenido, leida) VALUES
(4, 'vacuna', 'Recuerda que Max tiene su vacuna anual la próxima semana.', FALSE),
(3, 'adopcion', 'Se ha aprobado tu solicitud para pasear a Rocky este fin de semana.', TRUE);

INSERT INTO reseñas_centros (id_centro, id_usuario, puntuacion, comentario) VALUES
(1, 4, 5, 'Atención increíble, el veterinario fue muy amable con mi perro.'),
(2, 5, 4, 'Las instalaciones son buenas, pero hay mucha lista de espera para adoptar.');

INSERT INTO logros_usuario (id_usuario, id_logro) VALUES
(4, 1),
(5, 1),
(4, 2);

INSERT INTO inventario (id_usuario, id_item, cantidad) VALUES
(4, 1, 5),
(4, 2, 1),
(5, 1, 2);

-- ==============================================================================
-- NIVEL 2: Gestión y Actividad
-- ==============================================================================

INSERT INTO vacunas (id_mascota, nombre, fecha, proxima_dosis, notas) VALUES
(1, 'Rabia', '2023-06-15', '2024-06-15', 'Sin reacciones adversas.'),
(1, 'Polivalente', '2023-05-10', '2024-05-10', 'Administrada en clínica central.'),
(2, 'Leucemia Felina', '2023-09-01', '2024-09-01', 'Refuerzo necesario.');

INSERT INTO paseos (id_mascota, id_usuario, duracion, distancia, ruta) VALUES
(1, 4, 45, 3.2, '[{"lat": 37.1773, "lng": -3.5985}, {"lat": 37.1780, "lng": -3.5990}, {"lat": 37.1775, "lng": -3.6001}]'),
(3, 3, 60, 5.0, '[{"lat": 40.4167, "lng": -3.7037}, {"lat": 40.4170, "lng": -3.7040}]');

INSERT INTO alquiler (id_mascota, id_voluntario, fecha_inicio, fecha_fin, estado) VALUES
(3, 3, '2023-10-01', '2023-10-31', 'finalizado'),
(1, 4, '2024-03-01', NULL, 'activo');

INSERT INTO foro_hilos (id_categoria, id_usuario, titulo, contenido, vistas) VALUES
(2, 4, '¿Cómo preparar la casa para un cachorro?', 'Voy a adoptar un Golden y necesito consejos sobre qué comprar antes de que llegue.', 120),
(1, 2, 'Importancia de la desparasitación', 'Como veterinaria, quiero recordarles la importancia de desparasitar internamente cada 3 meses.', 340);

INSERT INTO campañas_donacion (id_mascota, titulo, descripcion, objetivo_dinero, estado, fecha_inicio) VALUES
(2, 'Operación dental para Luna', 'Luna necesita una limpieza y extracción dental urgente para poder comer sin dolor.', 300.00, 'abierta', '2024-02-15'),
(3, 'Silla de ruedas para Rocky', 'Ayudemos a Rocky a volver a correr con una silla adaptada a su tamaño.', 500.00, 'completada', '2023-11-01');

INSERT INTO mascotas_virtuales (id_usuario, id_mascota_real, nombre, especie, nivel, experiencia, hambre, felicidad, energia, higiene) VALUES
(4, 1, 'Maxi', 'Perro', 5, 1200, 80, 95, 60, 70),
(5, NULL, 'Sombra', 'Gato', 2, 350, 40, 50, 90, 30);

-- ==============================================================================
-- NIVEL 3: Interacción Profunda
-- ==============================================================================

INSERT INTO valoraciones (id_alquiler, id_usuario, puntuacion, comentario) VALUES
(1, 3, 5, 'Cuidar de Rocky fue una experiencia maravillosa, es un perro muy obediente.');

INSERT INTO consultas_chat (id_mascota, id_usuario, id_veterinario, id_alquiler, asunto, estado) VALUES
(1, 4, 2, 2, 'Duda sobre la alimentación de Max', 'activa'),
(2, 5, 2, NULL, 'Consulta sobre adopción de Luna', 'cerrada');

INSERT INTO foro_respuestas (id_hilo, id_usuario, contenido, votos) VALUES
(1, 3, 'Te recomiendo comprar una cama resistente a mordeduras y muchos juguetes interactivos.', 5),
(1, 2, 'Asegúrate de quitar cables del suelo y productos tóxicos de su alcance.', 12);

INSERT INTO donaciones (id_campaña, id_usuario, cantidad) VALUES
(1, 4, 50.00),
(1, 5, 25.00),
(2, 4, 100.00),
(2, 3, 400.00);

INSERT INTO acciones_tamagochi (id_mascota_virtual, tipo, puntos_ganados) VALUES
(1, 'alimentar', 10),
(1, 'jugar', 15),
(2, 'bañar', 20);

-- ==============================================================================
-- NIVEL 4 y 5: Mensajería
-- ==============================================================================

INSERT INTO mensajes (id_conversacion, id_emisor, contenido, leido) VALUES
(1, 4, 'Hola Laura, Max está comiendo menos pienso últimamente, ¿debería cambiarle la marca?', TRUE),
(1, 2, 'Hola Ana. ¿Ha tenido vómitos o diarrea? Si solo es falta de apetito, podemos probar a mezclarlo con comida húmeda.', TRUE),
(1, 4, 'No, todo lo demás es normal. Te adjunto una foto del pienso que uso.', FALSE),
(2, 5, 'Hola, me gustaría saber si Luna es compatible con otros gatos.', TRUE);

INSERT INTO archivos_adjuntos (id_mensaje, url_archivo, tipo) VALUES
(3, 'https://storage.pawlink.com/users/4/chat/foto_pienso.jpg', 'imagen');