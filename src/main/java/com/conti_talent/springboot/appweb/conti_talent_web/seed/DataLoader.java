package com.conti_talent.springboot.appweb.conti_talent_web.seed;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.*;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.Rol;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Carga inicial de datos en memoria. Replica EXACTAMENTE los datos que
 * actualmente vive en static/js/seed.js, manteniendo los mismos IDs
 * (u1, a1, o1, q1, p1...) y la misma estructura — para que el frontend
 * pueda apuntar al backend sin romper nada en su capa actual.
 *
 * Si en algún momento se migra a JPA basta con cambiar los Repositories
 * por los repositorios Spring Data; la lógica de seed seguirá funcionando.
 */
@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private static final long DAY_MS = 86_400_000L;

    private final UsuarioRepository usuarioRepository;
    private final AreaRepository areaRepository;
    private final OfertaRepository ofertaRepository;
    private final PreguntaRepository preguntaRepository;
    private final PostulanteRepository postulanteRepository;
    private final MetricasRepository metricasRepository;

    public DataLoader(UsuarioRepository usuarioRepository,
                      AreaRepository areaRepository,
                      OfertaRepository ofertaRepository,
                      PreguntaRepository preguntaRepository,
                      PostulanteRepository postulanteRepository,
                      MetricasRepository metricasRepository) {
        this.usuarioRepository = usuarioRepository;
        this.areaRepository = areaRepository;
        this.ofertaRepository = ofertaRepository;
        this.preguntaRepository = preguntaRepository;
        this.postulanteRepository = postulanteRepository;
        this.metricasRepository = metricasRepository;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.findAll().isEmpty()) {
            log.info("[DataLoader] Datos ya cargados; omito seed.");
            return;
        }
        log.info("[DataLoader] Cargando seed inicial de Conti Talent...");

        long now = System.currentTimeMillis();

        seedUsuarios(now);
        seedAreas();
        seedOfertas(now);
        seedPreguntas();
        seedPostulantes(now);
        seedMetricas();

        log.info("[DataLoader] Seed completado: {} usuarios, {} áreas, {} ofertas, {} preguntas, {} postulantes",
                usuarioRepository.findAll().size(),
                areaRepository.findAll().size(),
                ofertaRepository.findAll().size(),
                preguntaRepository.findAll().size(),
                postulanteRepository.findAll().size());
    }

    /* =================== USUARIOS =================== */
    private void seedUsuarios(long now) {
        save(new Usuario("u1", "Administrador", "Continental", "admin@contitalent.com", "admin123",  Rol.ADMIN,       true, now - DAY_MS * 30));
        save(new Usuario("u2", "Lucía",         "Ramos",       "lucia@example.com",     "lucia123",  Rol.POSTULANTE, true, now - DAY_MS * 8));
        save(new Usuario("u3", "Carlos",        "Mendoza",     "carlos@example.com",    "carlos123", Rol.POSTULANTE, true, now - DAY_MS * 5));
        save(new Usuario("u4", "María",         "Torres",      "maria@example.com",     "maria123",  Rol.POSTULANTE, true, now - DAY_MS * 2));
        save(new Usuario("u5", "Pedro",         "Salinas",     "pedro@example.com",     "pedro123",  Rol.POSTULANTE, true, now - DAY_MS * 7));
        save(new Usuario("u6", "Andrea",        "León",        "andrea@example.com",    "andrea123", Rol.POSTULANTE, true, now - DAY_MS * 10));
        save(new Usuario("u7", "Diego",         "Álvarez",     "diego@example.com",     "diego123",  Rol.POSTULANTE, true, now - DAY_MS * 15));
        save(new Usuario("u8", "Fiorella",      "Rojas",       "fiorella@example.com",  "fiora123",  Rol.POSTULANTE, true, now - DAY_MS * 8));
    }

    private void save(Usuario u) { usuarioRepository.save(u); }

    /* =================== ÁREAS =================== */
    private void seedAreas() {
        areaRepository.save(new Area("a1", "Ingeniería",                 "Sistemas, civil, industrial, mecatrónica y minas.",                                       "⚙️", "#6366f1"));
        areaRepository.save(new Area("a2", "Ciencias de la Empresa",     "Administración, contabilidad, marketing y negocios.",                                     "📊", "#06b6d4"));
        areaRepository.save(new Area("a3", "Derecho",                    "Derecho corporativo, civil, penal y constitucional.",                                     "⚖️", "#8b5cf6"));
        areaRepository.save(new Area("a4", "Humanidades",                "Comunicación, psicología y educación.",                                                   "📚", "#ec4899"));
        areaRepository.save(new Area("a5", "Ciencias de la Salud",       "Enfermería, psicología clínica y nutrición.",                                             "🩺", "#10b981"));
        areaRepository.save(new Area("a6", "Investigación y Desarrollo", "Centros de investigación e innovación universitaria.",                                    "🔬", "#f59e0b"));
        areaRepository.save(new Area("a7", "Tecnología y Sistemas",      "Equipo de TI institucional: infraestructura, soporte y software interno.",                "💻", "#0ea5e9"));
        areaRepository.save(new Area("a8", "Bienestar Universitario",    "Soporte estudiantil, deportes, cultura y atención psicopedagógica.",                      "🤝", "#f43f5e"));
    }

    /* =================== OFERTAS =================== */
    private void seedOfertas(long now) {
        ofertaRepository.save(new Oferta("o1",  "Profesor de Programación I",                "Trabajo",  "a1", "Presencial", "Huancayo", 2, true,
                "Docente para el curso de Programación I (Java) en la Escuela de Ingeniería de Sistemas. Diseña material, dicta clases y acompaña proyectos.",
                List.of("Ingeniero de Sistemas o afín", "2+ años enseñando o desarrollando software", "Manejo de Java y bases de datos", "Experiencia en metodologías ágiles"),
                List.of("Carga horaria flexible", "Capacitación pedagógica", "Convenios interinstitucionales"), now - DAY_MS * 3));

        ofertaRepository.save(new Oferta("o2",  "Práctica Pre-Profesional · Sistemas",       "Práctica", "a1", "Híbrido",    "Huancayo", 4, true,
                "Apoyo al área de Sistemas y TI de la universidad. Soporte, desarrollo de pequeñas mejoras y gestión de tickets.",
                List.of("Estudiante de Ingeniería de Sistemas", "Conocimientos de HTML, CSS y JS", "Buena comunicación"),
                List.of("Subvención económica", "Mentoría", "Certificación de prácticas"), now - DAY_MS * 2));

        ofertaRepository.save(new Oferta("o3",  "Profesor de Marketing Digital",             "Trabajo",  "a2", "Presencial", "Huancayo", 1, true,
                "Docente para Marketing Digital y Estrategia Comercial en el área de Ciencias de la Empresa.",
                List.of("Profesional en Marketing o Negocios", "Experiencia en performance digital", "Maestría (deseable)"),
                List.of("Plan de carrera docente", "Investigación remunerada"), now - DAY_MS * 4));

        ofertaRepository.save(new Oferta("o4",  "Práctica Pre-Profesional · Marketing",      "Práctica", "a2", "Presencial", "Huancayo", 3, true,
                "Apoyo en campañas digitales, contenidos y analítica para la marca Continental.",
                List.of("Estudiante de Marketing/Administración", "Manejo de redes sociales", "Curiosidad y proactividad"),
                List.of("Subvención económica", "Aprendizaje real", "Certificación"), now - DAY_MS));

        ofertaRepository.save(new Oferta("o5",  "Profesor de Derecho Constitucional",        "Trabajo",  "a3", "Presencial", "Huancayo", 1, false,
                "Docente para el curso de Derecho Constitucional en pregrado.",
                List.of("Abogado titulado", "Maestría o doctorado en Derecho", "Publicaciones académicas (deseable)"),
                List.of("Bonos por publicación", "Apoyo a investigación"), now - DAY_MS * 6));

        ofertaRepository.save(new Oferta("o6",  "Práctica Profesional · Derecho Civil",      "Práctica", "a3", "Híbrido",    "Huancayo", 2, false,
                "Apoyo al consultorio jurídico del área de Derecho.",
                List.of("Egresado o bachiller en Derecho", "Buen manejo de redacción"),
                List.of("Subvención", "Acompañamiento profesional"), now - DAY_MS * 5));

        ofertaRepository.save(new Oferta("o7",  "Profesor de Comunicación Oral y Escrita",   "Trabajo",  "a4", "Presencial", "Huancayo", 2, false,
                "Curso transversal en pregrado para todas las áreas académicas.",
                List.of("Licenciado en Comunicación o Educación", "Experiencia mínima 2 años"),
                List.of("Plan docente", "Becas para postgrado"), now - DAY_MS * 8));

        ofertaRepository.save(new Oferta("o8",  "Asistente de Investigación · Salud Pública","Práctica", "a5", "Híbrido",    "Huancayo", 2, false,
                "Apoyo a investigación de campo en proyectos de salud pública en Junín.",
                List.of("Estudiante de Ciencias de la Salud", "Estadística básica"),
                List.of("Subvención", "Co-autoría en publicaciones"), now - DAY_MS * 9));

        ofertaRepository.save(new Oferta("o9",  "Coordinador de Investigación",              "Trabajo",  "a6", "Presencial", "Huancayo", 1, false,
                "Lidera proyectos del Centro de Investigación de la Universidad Continental.",
                List.of("Magíster o doctor", "Publicaciones indexadas", "Liderazgo de equipos"),
                List.of("Sueldo competitivo", "Asignación de proyectos"), now - DAY_MS * 11));

        ofertaRepository.save(new Oferta("o10", "Práctica · Soporte de TI Universitario",    "Práctica", "a7", "Presencial", "Huancayo", 3, true,
                "Apoyo al equipo institucional de Tecnología y Sistemas: soporte a usuarios, gestión de equipos y mantenimiento de aulas digitales.",
                List.of("Estudios técnicos o universitarios en TI", "Conocimientos de redes y hardware", "Buena atención al usuario"),
                List.of("Subvención económica", "Certificación de prácticas", "Plan de mentoría"), now - DAY_MS));

        ofertaRepository.save(new Oferta("o11", "Coordinador de Bienestar Estudiantil",      "Trabajo",  "a8", "Presencial", "Huancayo", 1, false,
                "Lidera el equipo de Bienestar Universitario: programas de salud mental, deportes y vida estudiantil.",
                List.of("Profesional en Psicología, Educación o afín", "3+ años en gestión estudiantil", "Habilidades de liderazgo"),
                List.of("Plan de carrera", "Capacitación continua", "Contrato estable"), now - DAY_MS * 4));
    }

    /* =================== PREGUNTAS =================== */
    private void seedPreguntas() {
        // o1
        preguntaRepository.save(new Pregunta("q1", "o1", "¿Qué patrón de diseño aplicarías para desacoplar lógica de presentación en una aplicación web?",
                List.of("Singleton", "MVC", "Observer", "Factory"), 1));
        preguntaRepository.save(new Pregunta("q2", "o1", "¿Cuál es la principal ventaja de usar inyección de dependencias?",
                List.of("Mejor rendimiento en runtime", "Tests más sencillos y bajo acoplamiento", "Reduce el tamaño del bundle", "Reemplaza el uso de interfaces"), 1));
        preguntaRepository.save(new Pregunta("q3", "o1", "En SQL, ¿qué hace una sentencia INNER JOIN?",
                List.of("Retorna todas las filas de ambas tablas", "Retorna solo filas con coincidencias en ambas tablas", "Retorna filas únicas de la primera tabla", "Combina filas evitando duplicados"), 1));
        preguntaRepository.save(new Pregunta("q4", "o1", "En enseñanza universitaria, ¿qué metodología activa promueve el aprendizaje basado en proyectos?",
                List.of("Conferencia magistral", "Aprendizaje basado en proyectos (ABP)", "Examen oral", "Tutoriales pre-grabados"), 1));
        preguntaRepository.save(new Pregunta("q5", "o1", "¿Cuál NO es un método HTTP idempotente?",
                List.of("GET", "PUT", "DELETE", "POST"), 3));

        // o2
        preguntaRepository.save(new Pregunta("q6", "o2", "¿Qué etiqueta HTML5 representa contenido principal de la página?",
                List.of("<section>", "<main>", "<article>", "<div>"), 1));
        preguntaRepository.save(new Pregunta("q7", "o2", "¿Qué propiedad CSS usarías para alinear elementos en un eje horizontal con flex?",
                List.of("align-items", "justify-content", "flex-wrap", "grid-template"), 1));
        preguntaRepository.save(new Pregunta("q8", "o2", "¿Cuál es una buena práctica para evitar XSS en aplicaciones web?",
                List.of("Concatenar HTML con datos del usuario", "Escapar la salida y validar entradas", "Usar localStorage", "Cifrar las URLs"), 1));

        // o3
        preguntaRepository.save(new Pregunta("q9",  "o3", "¿Qué métrica mide el costo de adquirir un cliente?",
                List.of("CTR", "CAC", "ROI", "CPM"), 1));
        preguntaRepository.save(new Pregunta("q10", "o3", "¿Cuál es el propósito principal del SEO técnico?",
                List.of("Crear contenido viral", "Optimizar la indexación y el rendimiento del sitio", "Comprar enlaces", "Diseñar logos"), 1));
        preguntaRepository.save(new Pregunta("q11", "o3", "En un funnel de marketing, ¿qué etapa va antes de la decisión?",
                List.of("Awareness", "Consideration", "Retention", "Loyalty"), 1));

        // o4
        preguntaRepository.save(new Pregunta("q12", "o4", "¿Qué herramienta es estándar para diseño de piezas de redes sociales?",
                List.of("Figma", "Photoshop", "Excel", "Notepad"), 0));
        preguntaRepository.save(new Pregunta("q13", "o4", "¿Qué red social tiene mayor alcance para B2B en Latinoamérica?",
                List.of("Pinterest", "LinkedIn", "TikTok", "Snapchat"), 1));
    }

    /* =================== POSTULANTES =================== */
    private void seedPostulantes(long now) {
        Postulante p1 = newPostulante("p1", "u2", "o2", "Lucía Ramos", "lucia@example.com", "+51 987 654 321",
                "3 años apoyando áreas de TI en universidades", "JavaScript, soporte técnico, atención al usuario",
                "lucia_cv.pdf", EstadoPostulante.EN_EVALUACION, 67, now - DAY_MS * 4,
                Map.of("q6", 1, "q7", 1, "q8", 0));
        postulanteRepository.save(p1);

        Postulante p2 = newPostulante("p2", "u3", "o1", "Carlos Mendoza", "carlos@example.com", "+51 911 222 333",
                "5 años en arquitectura de software y docencia", "Java, Spring, Docker, didáctica universitaria",
                "carlos_cv.pdf", EstadoPostulante.APROBADO_TECNICO, 100, now - DAY_MS * 3,
                Map.of("q1", 1, "q2", 1, "q3", 1, "q4", 1, "q5", 3));
        postulanteRepository.save(p2);

        Postulante p3 = newPostulante("p3", "u4", "o4", "María Torres", "maria@example.com", "+51 933 555 777",
                "Estudiante de Marketing en último ciclo", "Community management, creación de contenido",
                "maria_cv.pdf", EstadoPostulante.POSTULADO, 0, now - DAY_MS, Map.of());
        postulanteRepository.save(p3);

        Postulante p4 = newPostulante("p4", "u5", "o2", "Pedro Salinas", "pedro@example.com", "+51 922 444 666",
                "Estudiante de Sistemas con prácticas previas", "JavaScript, HTML, CSS, soporte",
                "pedro_cv.pdf", EstadoPostulante.ENTREVISTA, 100, now - DAY_MS * 6,
                Map.of("q6", 1, "q7", 1, "q8", 1));
        postulanteRepository.save(p4);

        Postulante p5 = newPostulante("p5", "u6", "o3", "Andrea León", "andrea@example.com", "+51 944 888 111",
                "6 años en marketing B2B y docencia universitaria", "Estrategia digital, didáctica, public speaking",
                "andrea_cv.pdf", EstadoPostulante.EVALUACION_PSICOLOGICA, 100, now - DAY_MS * 9,
                Map.of("q9", 1, "q10", 1, "q11", 1));
        postulanteRepository.save(p5);

        Postulante p6 = newPostulante("p6", "u7", "o3", "Diego Álvarez", "diego@example.com", "+51 955 777 222",
                "8 años liderando agencias de marketing digital", "Ads, SEO, analítica, formación de equipos",
                "diego_cv.pdf", EstadoPostulante.ACEPTADO, 100, now - DAY_MS * 14,
                Map.of("q9", 1, "q10", 1, "q11", 1));
        postulanteRepository.save(p6);

        Postulante p7 = newPostulante("p7", "u8", "o4", "Fiorella Rojas", "fiorella@example.com", "+51 966 333 444",
                "1 año en diseño gráfico", "Figma, Illustrator",
                "fiorella_cv.pdf", EstadoPostulante.RECHAZADO, 50, now - DAY_MS * 7,
                Map.of("q12", 0, "q13", 0));
        postulanteRepository.save(p7);
    }

    private Postulante newPostulante(String id, String usuarioId, String ofertaId, String nombre, String email,
                                     String telefono, String experiencia, String habilidades, String cv,
                                     EstadoPostulante estado, int puntaje, long creadoEn,
                                     Map<String, Integer> respuestas) {
        Postulante p = new Postulante();
        p.setId(id);
        p.setUsuarioId(usuarioId);
        p.setOfertaId(ofertaId);
        p.setNombre(nombre);
        p.setEmail(email);
        p.setTelefono(telefono);
        p.setExperiencia(experiencia);
        p.setHabilidades(habilidades);
        p.setCv(cv);
        p.setEstado(estado);
        p.setPuntaje(puntaje);
        p.setRespuestas(new HashMap<>(respuestas));
        p.setCreadoEn(creadoEn);
        return p;
    }

    /* =================== MÉTRICAS =================== */
    private void seedMetricas() {
        MetricasDTO m = new MetricasDTO();
        Map<String, MetricasDTO.Serie> series = new LinkedHashMap<>();

        series.put("postulaciones", new MetricasDTO.Serie(
                "Postulaciones recibidas (últimos 8 meses)", "postulantes",
                List.of(
                        new MetricasDTO.Punto("sep-25", 125), new MetricasDTO.Punto("oct-25", 143),
                        new MetricasDTO.Punto("nov-25", 132), new MetricasDTO.Punto("dic-25", 156),
                        new MetricasDTO.Punto("ene-26", 162), new MetricasDTO.Punto("feb-26", 148),
                        new MetricasDTO.Punto("mar-26", 171), new MetricasDTO.Punto("abr-26", 183))));

        series.put("contrataciones", new MetricasDTO.Serie(
                "Contrataciones efectivas", "personas",
                List.of(
                        new MetricasDTO.Punto("sep-25", 12), new MetricasDTO.Punto("oct-25", 18),
                        new MetricasDTO.Punto("nov-25", 14), new MetricasDTO.Punto("dic-25", 22),
                        new MetricasDTO.Punto("ene-26", 25), new MetricasDTO.Punto("feb-26", 19),
                        new MetricasDTO.Punto("mar-26", 28), new MetricasDTO.Punto("abr-26", 31))));

        series.put("tiempoCierre", new MetricasDTO.Serie(
                "Tiempo promedio de cierre (días)", "días",
                List.of(
                        new MetricasDTO.Punto("sep-25", 28), new MetricasDTO.Punto("oct-25", 26),
                        new MetricasDTO.Punto("nov-25", 27), new MetricasDTO.Punto("dic-25", 24),
                        new MetricasDTO.Punto("ene-26", 22), new MetricasDTO.Punto("feb-26", 23),
                        new MetricasDTO.Punto("mar-26", 21), new MetricasDTO.Punto("abr-26", 19))));

        series.put("puntajePromedio", new MetricasDTO.Serie(
                "Puntaje promedio en evaluación técnica", "pts",
                List.of(
                        new MetricasDTO.Punto("sep-25", 70), new MetricasDTO.Punto("oct-25", 72),
                        new MetricasDTO.Punto("nov-25", 71), new MetricasDTO.Punto("dic-25", 74),
                        new MetricasDTO.Punto("ene-26", 76), new MetricasDTO.Punto("feb-26", 75),
                        new MetricasDTO.Punto("mar-26", 78), new MetricasDTO.Punto("abr-26", 81))));

        series.put("tasaAceptacion", new MetricasDTO.Serie(
                "Tasa de aceptación (%)", "%",
                List.of(
                        new MetricasDTO.Punto("sep-25", 9.6), new MetricasDTO.Punto("oct-25", 12.6),
                        new MetricasDTO.Punto("nov-25", 10.6), new MetricasDTO.Punto("dic-25", 14.1),
                        new MetricasDTO.Punto("ene-26", 15.4), new MetricasDTO.Punto("feb-26", 12.8),
                        new MetricasDTO.Punto("mar-26", 16.4), new MetricasDTO.Punto("abr-26", 16.9))));

        m.setSeries(series);

        MetricasDTO.EstadoActual estado = new MetricasDTO.EstadoActual();
        estado.setPostulantesActivos(183);
        estado.setOfertasAbiertas(12);
        estado.setEntrevistasHoy(7);
        estado.setOfertasEsteMes(31);
        estado.setTiempoPromedio("19 días");
        m.setEstadoActual(estado);

        metricasRepository.save(m);
    }
}
