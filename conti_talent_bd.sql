CREATE DATABASE IF NOT EXISTS conti_talent
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE conti_talent;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS tbl_postulante_respuesta;
DROP TABLE IF EXISTS tbl_postulante;
DROP TABLE IF EXISTS tbl_pregunta_opcion;
DROP TABLE IF EXISTS tbl_pregunta;
DROP TABLE IF EXISTS tbl_oferta_beneficio;
DROP TABLE IF EXISTS tbl_oferta_requisito;
DROP TABLE IF EXISTS tbl_oferta;
DROP TABLE IF EXISTS tbl_usuario;
DROP TABLE IF EXISTS tbl_estado;
DROP TABLE IF EXISTS tbl_rol;
DROP TABLE IF EXISTS tbl_area;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE tbl_area (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  icono VARCHAR(30),
  color VARCHAR(10),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_rol (
  id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(30) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  activo BIT(1) NOT NULL,
  creado_en BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_rol_codigo UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_estado (
  id BIGINT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(40) NOT NULL,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  orden INT NOT NULL,
  terminal BIT(1) NOT NULL,
  activo BIT(1) NOT NULL,
  creado_en BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_estado_codigo UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_usuario (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL,
  apellido VARCHAR(60),
  email VARCHAR(120) NOT NULL,
  password VARCHAR(255) NOT NULL,
  rol_id BIGINT NOT NULL,
  activo BIT(1) NOT NULL,
  creado_en BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_usuario_email UNIQUE (email),
  CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES tbl_rol (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_oferta (
  id BIGINT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(120) NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  area_id BIGINT NOT NULL,
  modalidad VARCHAR(20),
  ubicacion VARCHAR(80),
  vacantes INT NOT NULL,
  destacada BIT(1) NOT NULL,
  descripcion TEXT,
  creada_en BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_oferta_area FOREIGN KEY (area_id) REFERENCES tbl_area (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_oferta_requisito (
  oferta_id BIGINT NOT NULL,
  orden INT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  PRIMARY KEY (oferta_id, orden),
  CONSTRAINT fk_requisito_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_oferta_beneficio (
  oferta_id BIGINT NOT NULL,
  orden INT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  PRIMARY KEY (oferta_id, orden),
  CONSTRAINT fk_beneficio_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_pregunta (
  id BIGINT NOT NULL AUTO_INCREMENT,
  oferta_id BIGINT NOT NULL,
  enunciado VARCHAR(500) NOT NULL,
  correcta INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_pregunta_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_pregunta_opcion (
  pregunta_id BIGINT NOT NULL,
  indice INT NOT NULL,
  texto VARCHAR(255) NOT NULL,
  PRIMARY KEY (pregunta_id, indice),
  CONSTRAINT fk_opcion_pregunta FOREIGN KEY (pregunta_id) REFERENCES tbl_pregunta (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_postulante (
  id BIGINT NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT,
  oferta_id BIGINT NOT NULL,
  estado_id BIGINT NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL,
  telefono VARCHAR(30),
  experiencia TEXT,
  habilidades TEXT,
  cv VARCHAR(255),
  puntaje INT NOT NULL,
  creado_en BIGINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_postulante_usuario FOREIGN KEY (usuario_id) REFERENCES tbl_usuario (id),
  CONSTRAINT fk_postulante_oferta FOREIGN KEY (oferta_id) REFERENCES tbl_oferta (id),
  CONSTRAINT fk_postulante_estado FOREIGN KEY (estado_id) REFERENCES tbl_estado (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_postulante_respuesta (
  postulante_id BIGINT NOT NULL,
  pregunta_id BIGINT NOT NULL,
  opcion_elegida INT NOT NULL,
  PRIMARY KEY (postulante_id, pregunta_id),
  CONSTRAINT fk_respuesta_postulante FOREIGN KEY (postulante_id) REFERENCES tbl_postulante (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @ahora := CAST(UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000 AS UNSIGNED);
SET @dia := 86400000;

INSERT INTO tbl_rol (id, codigo, nombre, descripcion, activo, creado_en) VALUES
(1, 'ADMIN', 'Administrador', 'Acceso total al sistema: gestion de usuarios, areas, ofertas, postulantes y metricas.', b'1', @ahora - (@dia * 60)),
(2, 'POSTULANTE', 'Postulante', 'Usuario externo que postula a las ofertas publicadas por la institucion.', b'1', @ahora - (@dia * 60));

INSERT INTO tbl_estado (id, codigo, nombre, descripcion, orden, terminal, activo, creado_en) VALUES
(1, 'POSTULADO', 'Postulado', 'Postulacion recibida y pendiente de evaluacion tecnica.', 1, b'0', b'1', @ahora - (@dia * 60)),
(2, 'EN_EVALUACION', 'En evaluacion', 'El postulante rindio la prueba pero no alcanzo el umbral de aprobacion.', 2, b'0', b'1', @ahora - (@dia * 60)),
(3, 'APROBADO_TECNICO', 'Aprobado tecnico', 'Aprobo la evaluacion tecnica y avanza a entrevista.', 3, b'0', b'1', @ahora - (@dia * 60)),
(4, 'ENTREVISTA', 'Entrevista', 'Citado o citada para entrevista personal.', 4, b'0', b'1', @ahora - (@dia * 60)),
(5, 'EVALUACION_PSICOLOGICA', 'Evaluacion psicologica', 'Aprobada la entrevista, paso a evaluacion psicologica.', 5, b'0', b'1', @ahora - (@dia * 60)),
(6, 'ACEPTADO', 'Aceptado', 'Proceso completo. Candidato aceptado para la posicion.', 6, b'1', b'1', @ahora - (@dia * 60)),
(7, 'RECHAZADO', 'Rechazado', 'Candidato descartado en alguna etapa del proceso.', 7, b'1', b'1', @ahora - (@dia * 60));

INSERT INTO tbl_usuario (id, nombre, apellido, email, password, rol_id, activo, creado_en) VALUES
(1, 'Administrador', 'Continental', 'admin@contitalent.com', 'admin123', 1, b'1', @ahora - (@dia * 30)),
(2, 'Lucia', 'Ramos', 'lucia@example.com', 'lucia123', 2, b'1', @ahora - (@dia * 8)),
(3, 'Carlos', 'Mendoza', 'carlos@example.com', 'carlos123', 2, b'1', @ahora - (@dia * 5)),
(4, 'Maria', 'Torres', 'maria@example.com', 'maria123', 2, b'1', @ahora - (@dia * 2)),
(5, 'Pedro', 'Salinas', 'pedro@example.com', 'pedro123', 2, b'1', @ahora - (@dia * 7)),
(6, 'Andrea', 'Leon', 'andrea@example.com', 'andrea123', 2, b'1', @ahora - (@dia * 10)),
(7, 'Diego', 'Alvarez', 'diego@example.com', 'diego123', 2, b'1', @ahora - (@dia * 15)),
(8, 'Fiorella', 'Rojas', 'fiorella@example.com', 'fiora123', 2, b'1', @ahora - (@dia * 8));

INSERT INTO tbl_area (id, nombre, descripcion, icono, color) VALUES
(1, 'Ingenieria', 'Sistemas, civil, industrial, mecatronica y minas.', 'engineering', '#6366f1'),
(2, 'Ciencias de la Empresa', 'Administracion, contabilidad, marketing y negocios.', 'chart', '#06b6d4'),
(3, 'Derecho', 'Derecho corporativo, civil, penal y constitucional.', 'scale', '#8b5cf6'),
(4, 'Humanidades', 'Comunicacion, psicologia y educacion.', 'books', '#ec4899'),
(5, 'Ciencias de la Salud', 'Enfermeria, psicologia clinica y nutricion.', 'stethoscope', '#10b981'),
(6, 'Investigacion y Desarrollo', 'Centros de investigacion e innovacion universitaria.', 'microscope', '#f59e0b'),
(7, 'Tecnologia y Sistemas', 'Equipo de TI institucional: infraestructura, soporte y software interno.', 'computer', '#0ea5e9'),
(8, 'Bienestar Universitario', 'Soporte estudiantil, deportes, cultura y atencion psicopedagogica.', 'handshake', '#f43f5e');

INSERT INTO tbl_oferta (id, titulo, tipo, area_id, modalidad, ubicacion, vacantes, destacada, descripcion, creada_en) VALUES
(1, 'Profesor de Programacion I', 'Trabajo', 1, 'Presencial', 'Huancayo', 2, b'1', 'Docente para el curso de Programacion I (Java) en la Escuela de Ingenieria de Sistemas.', @ahora - (@dia * 3)),
(2, 'Practica Pre-Profesional Sistemas', 'Practica', 1, 'Hibrido', 'Huancayo', 4, b'1', 'Apoyo al area de Sistemas y TI: soporte, desarrollo de pequenias mejoras y gestion de tickets.', @ahora - (@dia * 2)),
(3, 'Profesor de Marketing Digital', 'Trabajo', 2, 'Presencial', 'Huancayo', 1, b'1', 'Docente para Marketing Digital y Estrategia Comercial.', @ahora - (@dia * 4)),
(4, 'Practica Pre-Profesional Marketing', 'Practica', 2, 'Presencial', 'Huancayo', 3, b'1', 'Apoyo en campanias digitales, contenidos y analitica para la marca Continental.', @ahora - @dia),
(5, 'Profesor de Derecho Constitucional', 'Trabajo', 3, 'Presencial', 'Huancayo', 1, b'0', 'Docente para el curso de Derecho Constitucional.', @ahora - (@dia * 6)),
(6, 'Practica Profesional Derecho Civil', 'Practica', 3, 'Hibrido', 'Huancayo', 2, b'0', 'Apoyo al consultorio juridico del area de Derecho.', @ahora - (@dia * 5)),
(7, 'Profesor de Comunicacion Oral y Escrita', 'Trabajo', 4, 'Presencial', 'Huancayo', 2, b'0', 'Curso transversal en pregrado.', @ahora - (@dia * 8)),
(8, 'Asistente de Investigacion Salud Publica', 'Practica', 5, 'Hibrido', 'Huancayo', 2, b'0', 'Apoyo a investigacion de campo en proyectos de salud publica.', @ahora - (@dia * 9)),
(9, 'Coordinador de Investigacion', 'Trabajo', 6, 'Presencial', 'Huancayo', 1, b'0', 'Lidera proyectos del Centro de Investigacion.', @ahora - (@dia * 11)),
(10, 'Practica Soporte de TI Universitario', 'Practica', 7, 'Presencial', 'Huancayo', 3, b'1', 'Apoyo al equipo institucional de Tecnologia y Sistemas.', @ahora - @dia),
(11, 'Coordinador de Bienestar Estudiantil', 'Trabajo', 8, 'Presencial', 'Huancayo', 1, b'0', 'Lidera el equipo de Bienestar Universitario.', @ahora - (@dia * 4));

INSERT INTO tbl_oferta_requisito (oferta_id, orden, texto) VALUES
(1, 0, 'Ingeniero de Sistemas o afin'), (1, 1, '2+ anios enseniando o desarrollando software'), (1, 2, 'Manejo de Java y bases de datos'), (1, 3, 'Experiencia en metodologias agiles'),
(2, 0, 'Estudiante de Ingenieria de Sistemas'), (2, 1, 'Conocimientos de HTML, CSS y JS'), (2, 2, 'Buena comunicacion'),
(3, 0, 'Profesional en Marketing o Negocios'), (3, 1, 'Experiencia en performance digital'), (3, 2, 'Maestria (deseable)'),
(4, 0, 'Estudiante de Marketing/Administracion'), (4, 1, 'Manejo de redes sociales'), (4, 2, 'Curiosidad y proactividad'),
(5, 0, 'Abogado titulado'), (5, 1, 'Maestria o doctorado en Derecho'), (5, 2, 'Publicaciones academicas'),
(6, 0, 'Egresado o bachiller en Derecho'), (6, 1, 'Buen manejo de redaccion'),
(7, 0, 'Licenciado en Comunicacion o Educacion'), (7, 1, 'Experiencia minima 2 anios'),
(8, 0, 'Estudiante de Ciencias de la Salud'), (8, 1, 'Estadistica basica'),
(9, 0, 'Magister o doctor'), (9, 1, 'Publicaciones indexadas'), (9, 2, 'Liderazgo de equipos'),
(10, 0, 'Estudios tecnicos o universitarios en TI'), (10, 1, 'Conocimientos de redes y hardware'), (10, 2, 'Buena atencion al usuario'),
(11, 0, 'Profesional en Psicologia, Educacion o afin'), (11, 1, '3+ anios en gestion estudiantil'), (11, 2, 'Habilidades de liderazgo');

INSERT INTO tbl_oferta_beneficio (oferta_id, orden, texto) VALUES
(1, 0, 'Carga horaria flexible'), (1, 1, 'Capacitacion pedagogica'), (1, 2, 'Convenios interinstitucionales'),
(2, 0, 'Subvencion economica'), (2, 1, 'Mentoria'), (2, 2, 'Certificacion de practicas'),
(3, 0, 'Plan de carrera docente'), (3, 1, 'Investigacion remunerada'),
(4, 0, 'Subvencion economica'), (4, 1, 'Aprendizaje real'), (4, 2, 'Certificacion'),
(5, 0, 'Bonos por publicacion'), (5, 1, 'Apoyo a investigacion'),
(6, 0, 'Subvencion'), (6, 1, 'Acompaniamiento profesional'),
(7, 0, 'Plan docente'), (7, 1, 'Becas para postgrado'),
(8, 0, 'Subvencion'), (8, 1, 'Co-autoria en publicaciones'),
(9, 0, 'Sueldo competitivo'), (9, 1, 'Asignacion de proyectos'),
(10, 0, 'Subvencion economica'), (10, 1, 'Certificacion de practicas'), (10, 2, 'Plan de mentoria'),
(11, 0, 'Plan de carrera'), (11, 1, 'Capacitacion continua'), (11, 2, 'Contrato estable');

INSERT INTO tbl_pregunta (id, oferta_id, enunciado, correcta) VALUES
(1, 1, 'Que patron de diseno desacopla logica de presentacion?', 1),
(2, 1, 'Cual es la principal ventaja de la inyeccion de dependencias?', 1),
(3, 1, 'Que hace una sentencia INNER JOIN en SQL?', 1),
(4, 1, 'Que metodologia activa promueve aprender por proyectos?', 1),
(5, 1, 'Cual NO es un metodo HTTP idempotente?', 3),
(6, 2, 'Que etiqueta HTML5 representa contenido principal?', 1),
(7, 2, 'Que propiedad CSS alinea elementos en eje horizontal con flex?', 1),
(8, 2, 'Buena practica para evitar XSS?', 1),
(9, 3, 'Que metrica mide el costo de adquirir un cliente?', 1),
(10, 3, 'Cual es el proposito principal del SEO tecnico?', 1),
(11, 3, 'En un funnel, que etapa va antes de la decision?', 1),
(12, 4, 'Herramienta estandar para piezas de redes sociales?', 0),
(13, 4, 'Red social con mayor alcance B2B en LATAM?', 1);

INSERT INTO tbl_pregunta_opcion (pregunta_id, indice, texto) VALUES
(1, 0, 'Singleton'), (1, 1, 'MVC'), (1, 2, 'Observer'), (1, 3, 'Factory'),
(2, 0, 'Mejor rendimiento en runtime'), (2, 1, 'Tests mas sencillos y bajo acoplamiento'), (2, 2, 'Reduce el bundle'), (2, 3, 'Reemplaza interfaces'),
(3, 0, 'Retorna todas las filas de ambas tablas'), (3, 1, 'Retorna filas con coincidencia en ambas tablas'), (3, 2, 'Retorna filas unicas'), (3, 3, 'Combina filas evitando duplicados'),
(4, 0, 'Conferencia magistral'), (4, 1, 'Aprendizaje basado en proyectos'), (4, 2, 'Examen oral'), (4, 3, 'Tutoriales pre-grabados'),
(5, 0, 'GET'), (5, 1, 'PUT'), (5, 2, 'DELETE'), (5, 3, 'POST'),
(6, 0, '<section>'), (6, 1, '<main>'), (6, 2, '<article>'), (6, 3, '<div>'),
(7, 0, 'align-items'), (7, 1, 'justify-content'), (7, 2, 'flex-wrap'), (7, 3, 'grid-template'),
(8, 0, 'Concatenar HTML con datos del usuario'), (8, 1, 'Escapar la salida y validar entradas'), (8, 2, 'Usar localStorage'), (8, 3, 'Cifrar las URLs'),
(9, 0, 'CTR'), (9, 1, 'CAC'), (9, 2, 'ROI'), (9, 3, 'CPM'),
(10, 0, 'Crear contenido viral'), (10, 1, 'Optimizar la indexacion y rendimiento'), (10, 2, 'Comprar enlaces'), (10, 3, 'Disenar logos'),
(11, 0, 'Awareness'), (11, 1, 'Consideration'), (11, 2, 'Retention'), (11, 3, 'Loyalty'),
(12, 0, 'Figma'), (12, 1, 'Photoshop'), (12, 2, 'Excel'), (12, 3, 'Notepad'),
(13, 0, 'Pinterest'), (13, 1, 'LinkedIn'), (13, 2, 'TikTok'), (13, 3, 'Snapchat');

INSERT INTO tbl_postulante (id, usuario_id, oferta_id, estado_id, nombre, email, telefono, experiencia, habilidades, cv, puntaje, creado_en) VALUES
(1, 2, 2, 2, 'Lucia Ramos', 'lucia@example.com', '+51 987 654 321', '3 anios apoyando areas de TI en universidades', 'JavaScript, soporte tecnico, atencion al usuario', 'lucia_cv.pdf', 67, @ahora - (@dia * 4)),
(2, 3, 1, 3, 'Carlos Mendoza', 'carlos@example.com', '+51 911 222 333', '5 anios en arquitectura de software y docencia', 'Java, Spring, Docker, didactica universitaria', 'carlos_cv.pdf', 100, @ahora - (@dia * 3)),
(3, 4, 4, 1, 'Maria Torres', 'maria@example.com', '+51 933 555 777', 'Estudiante de Marketing en ultimo ciclo', 'Community management, creacion de contenido', 'maria_cv.pdf', 0, @ahora - @dia),
(4, 5, 2, 4, 'Pedro Salinas', 'pedro@example.com', '+51 922 444 666', 'Estudiante de Sistemas con practicas previas', 'JavaScript, HTML, CSS, soporte', 'pedro_cv.pdf', 100, @ahora - (@dia * 6)),
(5, 6, 3, 5, 'Andrea Leon', 'andrea@example.com', '+51 944 888 111', '6 anios en marketing B2B y docencia universitaria', 'Estrategia digital, didactica, public speaking', 'andrea_cv.pdf', 100, @ahora - (@dia * 9)),
(6, 7, 3, 6, 'Diego Alvarez', 'diego@example.com', '+51 955 777 222', '8 anios liderando agencias de marketing digital', 'Ads, SEO, analitica, formacion de equipos', 'diego_cv.pdf', 100, @ahora - (@dia * 14)),
(7, 8, 4, 7, 'Fiorella Rojas', 'fiorella@example.com', '+51 966 333 444', '1 anio en diseno grafico', 'Figma, Illustrator', 'fiorella_cv.pdf', 50, @ahora - (@dia * 7));

INSERT INTO tbl_postulante_respuesta (postulante_id, pregunta_id, opcion_elegida) VALUES
(1, 6, 1), (1, 7, 1), (1, 8, 0),
(2, 1, 1), (2, 2, 1), (2, 3, 1), (2, 4, 1), (2, 5, 3),
(4, 6, 1), (4, 7, 1), (4, 8, 1),
(5, 9, 1), (5, 10, 1), (5, 11, 1),
(6, 9, 1), (6, 10, 1), (6, 11, 1),
(7, 12, 0), (7, 13, 0);

ALTER TABLE tbl_area AUTO_INCREMENT = 9;
ALTER TABLE tbl_rol AUTO_INCREMENT = 3;
ALTER TABLE tbl_estado AUTO_INCREMENT = 8;
ALTER TABLE tbl_usuario AUTO_INCREMENT = 9;
ALTER TABLE tbl_oferta AUTO_INCREMENT = 12;
ALTER TABLE tbl_pregunta AUTO_INCREMENT = 14;
ALTER TABLE tbl_postulante AUTO_INCREMENT = 8;
