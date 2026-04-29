/* =========================================================
   seed.js — Datos iniciales para Conti Talent
   Plataforma de la Universidad Continental Huancayo:
   ofrece prácticas y trabajos académicos a postulantes externos.
   ========================================================= */

const Seed = (() => {
  const SEED_FLAG = 'seeded_v4';

  const isSeeded   = () => Storage.read(SEED_FLAG, false) === true;
  const markSeeded = () => Storage.write(SEED_FLAG, true);

  /* ---------- USUARIOS ---------- */
  const seedUsuarios = () => ([
    { id: 'u1', nombre: 'Administrador', apellido: 'Continental', email: 'admin@contitalent.com', password: 'admin123',  rol: 'admin',      activo: true, creadoEn: Date.now() - 86400000 * 30 },
    { id: 'u2', nombre: 'Lucía',         apellido: 'Ramos',       email: 'lucia@example.com',     password: 'lucia123',  rol: 'postulante', activo: true, creadoEn: Date.now() - 86400000 * 8  },
    { id: 'u3', nombre: 'Carlos',        apellido: 'Mendoza',     email: 'carlos@example.com',    password: 'carlos123', rol: 'postulante', activo: true, creadoEn: Date.now() - 86400000 * 5  },
    { id: 'u4', nombre: 'María',         apellido: 'Torres',      email: 'maria@example.com',     password: 'maria123',  rol: 'postulante', activo: true, creadoEn: Date.now() - 86400000 * 2  },
    { id: 'u5', nombre: 'Pedro',         apellido: 'Salinas',     email: 'pedro@example.com',     password: 'pedro123',  rol: 'postulante', activo: true, creadoEn: Date.now() - 86400000 * 7  },
    { id: 'u6', nombre: 'Andrea',        apellido: 'León',        email: 'andrea@example.com',    password: 'andrea123', rol: 'postulante', activo: true, creadoEn: Date.now() - 86400000 * 10 },
    { id: 'u7', nombre: 'Diego',         apellido: 'Álvarez',     email: 'diego@example.com',     password: 'diego123',  rol: 'postulante', activo: true, creadoEn: Date.now() - 86400000 * 15 },
    { id: 'u8', nombre: 'Fiorella',      apellido: 'Rojas',       email: 'fiorella@example.com',  password: 'fiora123',  rol: 'postulante', activo: true, creadoEn: Date.now() - 86400000 * 8  }
  ]);

  /* ---------- ÁREAS (mezcla de facultades y áreas administrativas) ---------- */
  const seedAreas = () => ([
    { id: 'a1', nombre: 'Ingeniería',                  icono: '⚙️', descripcion: 'Sistemas, civil, industrial, mecatrónica y minas.',           color: '#6366f1' },
    { id: 'a2', nombre: 'Ciencias de la Empresa',      icono: '📊', descripcion: 'Administración, contabilidad, marketing y negocios.',         color: '#06b6d4' },
    { id: 'a3', nombre: 'Derecho',                     icono: '⚖️', descripcion: 'Derecho corporativo, civil, penal y constitucional.',         color: '#8b5cf6' },
    { id: 'a4', nombre: 'Humanidades',                 icono: '📚', descripcion: 'Comunicación, psicología y educación.',                       color: '#ec4899' },
    { id: 'a5', nombre: 'Ciencias de la Salud',        icono: '🩺', descripcion: 'Enfermería, psicología clínica y nutrición.',                 color: '#10b981' },
    { id: 'a6', nombre: 'Investigación y Desarrollo',  icono: '🔬', descripcion: 'Centros de investigación e innovación universitaria.',        color: '#f59e0b' },
    { id: 'a7', nombre: 'Tecnología y Sistemas',       icono: '💻', descripcion: 'Equipo de TI institucional: infraestructura, soporte y software interno.', color: '#0ea5e9' },
    { id: 'a8', nombre: 'Bienestar Universitario',     icono: '🤝', descripcion: 'Soporte estudiantil, deportes, cultura y atención psicopedagógica.',         color: '#f43f5e' }
  ]);

  /* ---------- OFERTAS (con campo `tipo`: Práctica | Trabajo) ---------- */
  const seedOfertas = () => ([
    { id: 'o1', titulo: 'Profesor de Programación I',                tipo: 'Trabajo',  areaId: 'a1', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 2, destacada: true,  descripcion: 'Docente para el curso de Programación I (Java) en la Escuela de Ingeniería de Sistemas. Diseña material, dicta clases y acompaña proyectos.', requisitos: ['Ingeniero de Sistemas o afín', '2+ años enseñando o desarrollando software', 'Manejo de Java y bases de datos', 'Experiencia en metodologías ágiles'], beneficios: ['Carga horaria flexible', 'Capacitación pedagógica', 'Convenios interinstitucionales'], creadaEn: Date.now() - 86400000 * 3 },
    { id: 'o2', titulo: 'Práctica Pre-Profesional · Sistemas',       tipo: 'Práctica', areaId: 'a1', modalidad: 'Híbrido',    ubicacion: 'Huancayo', vacantes: 4, destacada: true,  descripcion: 'Apoyo al área de Sistemas y TI de la universidad. Soporte, desarrollo de pequeñas mejoras y gestión de tickets.', requisitos: ['Estudiante de Ingeniería de Sistemas', 'Conocimientos de HTML, CSS y JS', 'Buena comunicación'], beneficios: ['Subvención económica', 'Mentoría', 'Certificación de prácticas'], creadaEn: Date.now() - 86400000 * 2 },
    { id: 'o3', titulo: 'Profesor de Marketing Digital',             tipo: 'Trabajo',  areaId: 'a2', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 1, destacada: true,  descripcion: 'Docente para Marketing Digital y Estrategia Comercial en el área de Ciencias de la Empresa.', requisitos: ['Profesional en Marketing o Negocios', 'Experiencia en performance digital', 'Maestría (deseable)'], beneficios: ['Plan de carrera docente', 'Investigación remunerada'], creadaEn: Date.now() - 86400000 * 4 },
    { id: 'o4', titulo: 'Práctica Pre-Profesional · Marketing',      tipo: 'Práctica', areaId: 'a2', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 3, destacada: true,  descripcion: 'Apoyo en campañas digitales, contenidos y analítica para la marca Continental.', requisitos: ['Estudiante de Marketing/Administración', 'Manejo de redes sociales', 'Curiosidad y proactividad'], beneficios: ['Subvención económica', 'Aprendizaje real', 'Certificación'], creadaEn: Date.now() - 86400000 * 1 },
    { id: 'o5', titulo: 'Profesor de Derecho Constitucional',        tipo: 'Trabajo',  areaId: 'a3', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 1, destacada: false, descripcion: 'Docente para el curso de Derecho Constitucional en pregrado.', requisitos: ['Abogado titulado', 'Maestría o doctorado en Derecho', 'Publicaciones académicas (deseable)'], beneficios: ['Bonos por publicación', 'Apoyo a investigación'], creadaEn: Date.now() - 86400000 * 6 },
    { id: 'o6', titulo: 'Práctica Profesional · Derecho Civil',      tipo: 'Práctica', areaId: 'a3', modalidad: 'Híbrido',    ubicacion: 'Huancayo', vacantes: 2, destacada: false, descripcion: 'Apoyo al consultorio jurídico del área de Derecho.', requisitos: ['Egresado o bachiller en Derecho', 'Buen manejo de redacción'], beneficios: ['Subvención', 'Acompañamiento profesional'], creadaEn: Date.now() - 86400000 * 5 },
    { id: 'o7', titulo: 'Profesor de Comunicación Oral y Escrita',   tipo: 'Trabajo',  areaId: 'a4', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 2, destacada: false, descripcion: 'Curso transversal en pregrado para todas las áreas académicas.', requisitos: ['Licenciado en Comunicación o Educación', 'Experiencia mínima 2 años'], beneficios: ['Plan docente', 'Becas para postgrado'], creadaEn: Date.now() - 86400000 * 8 },
    { id: 'o8', titulo: 'Asistente de Investigación · Salud Pública',tipo: 'Práctica', areaId: 'a5', modalidad: 'Híbrido',    ubicacion: 'Huancayo', vacantes: 2, destacada: false, descripcion: 'Apoyo a investigación de campo en proyectos de salud pública en Junín.', requisitos: ['Estudiante de Ciencias de la Salud', 'Estadística básica'], beneficios: ['Subvención', 'Co-autoría en publicaciones'], creadaEn: Date.now() - 86400000 * 9 },
    { id: 'o9',  titulo: 'Coordinador de Investigación',              tipo: 'Trabajo',  areaId: 'a6', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 1, destacada: false, descripcion: 'Lidera proyectos del Centro de Investigación de la Universidad Continental.', requisitos: ['Magíster o doctor', 'Publicaciones indexadas', 'Liderazgo de equipos'], beneficios: ['Sueldo competitivo', 'Asignación de proyectos'], creadaEn: Date.now() - 86400000 * 11 },
    { id: 'o10', titulo: 'Práctica · Soporte de TI Universitario',    tipo: 'Práctica', areaId: 'a7', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 3, destacada: true,  descripcion: 'Apoyo al equipo institucional de Tecnología y Sistemas: soporte a usuarios, gestión de equipos y mantenimiento de aulas digitales.', requisitos: ['Estudios técnicos o universitarios en TI', 'Conocimientos de redes y hardware', 'Buena atención al usuario'], beneficios: ['Subvención económica', 'Certificación de prácticas', 'Plan de mentoría'], creadaEn: Date.now() - 86400000 * 1 },
    { id: 'o11', titulo: 'Coordinador de Bienestar Estudiantil',      tipo: 'Trabajo',  areaId: 'a8', modalidad: 'Presencial', ubicacion: 'Huancayo', vacantes: 1, destacada: false, descripcion: 'Lidera el equipo de Bienestar Universitario: programas de salud mental, deportes y vida estudiantil.', requisitos: ['Profesional en Psicología, Educación o afín', '3+ años en gestión estudiantil', 'Habilidades de liderazgo'], beneficios: ['Plan de carrera', 'Capacitación continua', 'Contrato estable'], creadaEn: Date.now() - 86400000 * 4 }
  ]);

  /* ---------- PREGUNTAS ---------- */
  const seedPreguntas = () => ([
    /* o1 — Profesor de Programación I */
    { id: 'q1', ofertaId: 'o1', pregunta: '¿Qué patrón de diseño aplicarías para desacoplar lógica de presentación en una aplicación web?',     opciones: ['Singleton', 'MVC', 'Observer', 'Factory'], correcta: 1 },
    { id: 'q2', ofertaId: 'o1', pregunta: '¿Cuál es la principal ventaja de usar inyección de dependencias?',                                     opciones: ['Mejor rendimiento en runtime', 'Tests más sencillos y bajo acoplamiento', 'Reduce el tamaño del bundle', 'Reemplaza el uso de interfaces'], correcta: 1 },
    { id: 'q3', ofertaId: 'o1', pregunta: 'En SQL, ¿qué hace una sentencia INNER JOIN?',                                                          opciones: ['Retorna todas las filas de ambas tablas', 'Retorna solo filas con coincidencias en ambas tablas', 'Retorna filas únicas de la primera tabla', 'Combina filas evitando duplicados'], correcta: 1 },
    { id: 'q4', ofertaId: 'o1', pregunta: 'En enseñanza universitaria, ¿qué metodología activa promueve el aprendizaje basado en proyectos?',     opciones: ['Conferencia magistral', 'Aprendizaje basado en proyectos (ABP)', 'Examen oral', 'Tutoriales pre-grabados'], correcta: 1 },
    { id: 'q5', ofertaId: 'o1', pregunta: '¿Cuál NO es un método HTTP idempotente?',                                                              opciones: ['GET', 'PUT', 'DELETE', 'POST'], correcta: 3 },

    /* o2 — Práctica Sistemas */
    { id: 'q6',  ofertaId: 'o2', pregunta: '¿Qué etiqueta HTML5 representa contenido principal de la página?',                                    opciones: ['<section>', '<main>', '<article>', '<div>'], correcta: 1 },
    { id: 'q7',  ofertaId: 'o2', pregunta: '¿Qué propiedad CSS usarías para alinear elementos en un eje horizontal con flex?',                    opciones: ['align-items', 'justify-content', 'flex-wrap', 'grid-template'], correcta: 1 },
    { id: 'q8',  ofertaId: 'o2', pregunta: '¿Cuál es una buena práctica para evitar XSS en aplicaciones web?',                                    opciones: ['Concatenar HTML con datos del usuario', 'Escapar la salida y validar entradas', 'Usar localStorage', 'Cifrar las URLs'], correcta: 1 },

    /* o3 — Profesor Marketing Digital */
    { id: 'q9',  ofertaId: 'o3', pregunta: '¿Qué métrica mide el costo de adquirir un cliente?',                                                  opciones: ['CTR', 'CAC', 'ROI', 'CPM'], correcta: 1 },
    { id: 'q10', ofertaId: 'o3', pregunta: '¿Cuál es el propósito principal del SEO técnico?',                                                    opciones: ['Crear contenido viral', 'Optimizar la indexación y el rendimiento del sitio', 'Comprar enlaces', 'Diseñar logos'], correcta: 1 },
    { id: 'q11', ofertaId: 'o3', pregunta: 'En un funnel de marketing, ¿qué etapa va antes de la decisión?',                                      opciones: ['Awareness', 'Consideration', 'Retention', 'Loyalty'], correcta: 1 },

    /* o4 — Práctica Marketing */
    { id: 'q12', ofertaId: 'o4', pregunta: '¿Qué herramienta es estándar para diseño de piezas de redes sociales?',                                opciones: ['Figma', 'Photoshop', 'Excel', 'Notepad'], correcta: 0 },
    { id: 'q13', ofertaId: 'o4', pregunta: '¿Qué red social tiene mayor alcance para B2B en Latinoamérica?',                                       opciones: ['Pinterest', 'LinkedIn', 'TikTok', 'Snapchat'], correcta: 1 }
  ]);

  /* ---------- POSTULANTES ---------- */
  const seedPostulantes = () => ([
    { id: 'p1', usuarioId: 'u2', ofertaId: 'o2', nombre: 'Lucía Ramos',    email: 'lucia@example.com',    telefono: '+51 987 654 321', experiencia: '3 años apoyando áreas de TI en universidades',         habilidades: 'JavaScript, soporte técnico, atención al usuario', estado: 'EN_EVALUACION',         puntaje: 67,  cv: 'lucia_cv.pdf',    respuestas: { q6: 1, q7: 1, q8: 0 },                                       creadoEn: Date.now() - 86400000 * 4  },
    { id: 'p2', usuarioId: 'u3', ofertaId: 'o1', nombre: 'Carlos Mendoza', email: 'carlos@example.com',   telefono: '+51 911 222 333', experiencia: '5 años en arquitectura de software y docencia',       habilidades: 'Java, Spring, Docker, didáctica universitaria',    estado: 'APROBADO_TECNICO',       puntaje: 100, cv: 'carlos_cv.pdf',  respuestas: { q1: 1, q2: 1, q3: 1, q4: 1, q5: 3 },                          creadoEn: Date.now() - 86400000 * 3  },
    { id: 'p3', usuarioId: 'u4', ofertaId: 'o4', nombre: 'María Torres',   email: 'maria@example.com',    telefono: '+51 933 555 777', experiencia: 'Estudiante de Marketing en último ciclo',             habilidades: 'Community management, creación de contenido',     estado: 'POSTULADO',              puntaje: 0,   cv: 'maria_cv.pdf',   respuestas: {},                                                              creadoEn: Date.now() - 86400000 * 1  },
    { id: 'p4', usuarioId: 'u5', ofertaId: 'o2', nombre: 'Pedro Salinas',  email: 'pedro@example.com',    telefono: '+51 922 444 666', experiencia: 'Estudiante de Sistemas con prácticas previas',         habilidades: 'JavaScript, HTML, CSS, soporte',                  estado: 'ENTREVISTA',             puntaje: 100, cv: 'pedro_cv.pdf',   respuestas: { q6: 1, q7: 1, q8: 1 },                                        creadoEn: Date.now() - 86400000 * 6  },
    { id: 'p5', usuarioId: 'u6', ofertaId: 'o3', nombre: 'Andrea León',    email: 'andrea@example.com',   telefono: '+51 944 888 111', experiencia: '6 años en marketing B2B y docencia universitaria',     habilidades: 'Estrategia digital, didáctica, public speaking',  estado: 'EVALUACION_PSICOLOGICA', puntaje: 100, cv: 'andrea_cv.pdf',  respuestas: { q9: 1, q10: 1, q11: 1 },                                      creadoEn: Date.now() - 86400000 * 9  },
    { id: 'p6', usuarioId: 'u7', ofertaId: 'o3', nombre: 'Diego Álvarez',  email: 'diego@example.com',    telefono: '+51 955 777 222', experiencia: '8 años liderando agencias de marketing digital',       habilidades: 'Ads, SEO, analítica, formación de equipos',       estado: 'ACEPTADO',               puntaje: 100, cv: 'diego_cv.pdf',   respuestas: { q9: 1, q10: 1, q11: 1 },                                      creadoEn: Date.now() - 86400000 * 14 },
    { id: 'p7', usuarioId: 'u8', ofertaId: 'o4', nombre: 'Fiorella Rojas', email: 'fiorella@example.com', telefono: '+51 966 333 444', experiencia: '1 año en diseño gráfico',                              habilidades: 'Figma, Illustrator',                              estado: 'RECHAZADO',              puntaje: 50,  cv: 'fiorella_cv.pdf',respuestas: { q12: 0, q13: 0 },                                              creadoEn: Date.now() - 86400000 * 7  }
  ]);

  /* ---------- MÉTRICAS ---------- */
  const seedMetricas = () => ({
    series: {
      postulaciones:    { label: 'Postulaciones recibidas (últimos 8 meses)', unidad: 'postulantes',
        puntos: [
          { mes: 'sep-25', valor: 125 }, { mes: 'oct-25', valor: 143 }, { mes: 'nov-25', valor: 132 }, { mes: 'dic-25', valor: 156 },
          { mes: 'ene-26', valor: 162 }, { mes: 'feb-26', valor: 148 }, { mes: 'mar-26', valor: 171 }, { mes: 'abr-26', valor: 183 }
        ] },
      contrataciones:   { label: 'Contrataciones efectivas', unidad: 'personas',
        puntos: [
          { mes: 'sep-25', valor: 12 }, { mes: 'oct-25', valor: 18 }, { mes: 'nov-25', valor: 14 }, { mes: 'dic-25', valor: 22 },
          { mes: 'ene-26', valor: 25 }, { mes: 'feb-26', valor: 19 }, { mes: 'mar-26', valor: 28 }, { mes: 'abr-26', valor: 31 }
        ] },
      tiempoCierre:     { label: 'Tiempo promedio de cierre (días)', unidad: 'días',
        puntos: [
          { mes: 'sep-25', valor: 28 }, { mes: 'oct-25', valor: 26 }, { mes: 'nov-25', valor: 27 }, { mes: 'dic-25', valor: 24 },
          { mes: 'ene-26', valor: 22 }, { mes: 'feb-26', valor: 23 }, { mes: 'mar-26', valor: 21 }, { mes: 'abr-26', valor: 19 }
        ] },
      puntajePromedio:  { label: 'Puntaje promedio en evaluación técnica', unidad: 'pts',
        puntos: [
          { mes: 'sep-25', valor: 70 }, { mes: 'oct-25', valor: 72 }, { mes: 'nov-25', valor: 71 }, { mes: 'dic-25', valor: 74 },
          { mes: 'ene-26', valor: 76 }, { mes: 'feb-26', valor: 75 }, { mes: 'mar-26', valor: 78 }, { mes: 'abr-26', valor: 81 }
        ] },
      tasaAceptacion:   { label: 'Tasa de aceptación (%)', unidad: '%',
        puntos: [
          { mes: 'sep-25', valor: 9.6 }, { mes: 'oct-25', valor: 12.6 }, { mes: 'nov-25', valor: 10.6 }, { mes: 'dic-25', valor: 14.1 },
          { mes: 'ene-26', valor: 15.4 }, { mes: 'feb-26', valor: 12.8 }, { mes: 'mar-26', valor: 16.4 }, { mes: 'abr-26', valor: 16.9 }
        ] }
    },
    estadoActual: {
      postulantesActivos: 183,
      ofertasAbiertas: 12,
      entrevistasHoy: 7,
      ofertasEsteMes: 31,
      tiempoPromedio: '19 días'
    }
  });

  const run = () => {
    if (isSeeded()) return;
    Storage.write('areas',       seedAreas());
    Storage.write('ofertas',     seedOfertas());
    Storage.write('preguntas',   seedPreguntas());
    Storage.write('usuarios',    seedUsuarios());
    Storage.write('postulantes', seedPostulantes());
    Storage.write('metricas',    seedMetricas());
    Storage.clear('seeded_v1');
    Storage.clear('seeded_v2');
    Storage.clear('seeded_v3');
    markSeeded();
  };

  return { run };
})();

window.Seed = Seed;
