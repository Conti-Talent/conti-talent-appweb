package com.conti_talent.springboot.appweb.conti_talent_web.seed;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.MetricasDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.*;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.RolCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Carga inicial de datos en memoria. Replica los datos del seed.js del
 * frontend, pero ahora con dos catalogos extra: ROLES y ESTADOS, que se
 * cargan ANTES que las entidades que los referencian. Esto prepara la
 * migracion futura a base de datos relacional sin cambios en services.
 */
@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final long MILISEGUNDOS_POR_DIA = 86_400_000L;

    private final IRolRepository rolRepository;
    private final IEstadoRepository estadoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IAreaRepository areaRepository;
    private final IOfertaRepository ofertaRepository;
    private final IPreguntaRepository preguntaRepository;
    private final IPostulanteRepository postulanteRepository;
    private final IMetricasRepository metricasRepository;

    public DataLoader(IRolRepository rolRepository,
                      IEstadoRepository estadoRepository,
                      IUsuarioRepository usuarioRepository,
                      IAreaRepository areaRepository,
                      IOfertaRepository ofertaRepository,
                      IPreguntaRepository preguntaRepository,
                      IPostulanteRepository postulanteRepository,
                      IMetricasRepository metricasRepository) {
        this.rolRepository = rolRepository;
        this.estadoRepository = estadoRepository;
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

        long ahora = System.currentTimeMillis();

        cargarRoles(ahora);
        cargarEstados(ahora);
        cargarUsuarios(ahora);
        cargarAreas();
        cargarOfertas(ahora);
        cargarPreguntas();
        cargarPostulantes(ahora);
        cargarMetricas();

        log.info("[DataLoader] Seed completado: {} roles, {} estados, {} usuarios, {} areas, {} ofertas, {} preguntas, {} postulantes",
                rolRepository.listarTodos().size(),
                estadoRepository.listarTodos().size(),
                usuarioRepository.findAll().size(),
                areaRepository.findAll().size(),
                ofertaRepository.findAll().size(),
                preguntaRepository.findAll().size(),
                postulanteRepository.findAll().size());
    }

    /* =================== ROLES =================== */
    private void cargarRoles(long ahora) {
        rolRepository.guardar(new Rol("r1", RolCodigo.ADMIN.name(),
                "Administrador",
                "Acceso total al sistema: gestion de usuarios, areas, ofertas, postulantes y metricas.",
                true, ahora - MILISEGUNDOS_POR_DIA * 60));
        rolRepository.guardar(new Rol("r2", RolCodigo.POSTULANTE.name(),
                "Postulante",
                "Usuario externo que postula a las ofertas publicadas por la institucion.",
                true, ahora - MILISEGUNDOS_POR_DIA * 60));
    }

    /* =================== ESTADOS =================== */
    private void cargarEstados(long ahora) {
        estadoRepository.guardar(new Estado("e1", EstadoCodigo.POSTULADO.name(),
                "Postulado", "Postulacion recibida y pendiente de evaluacion tecnica.",
                1, false, true, ahora - MILISEGUNDOS_POR_DIA * 60));
        estadoRepository.guardar(new Estado("e2", EstadoCodigo.EN_EVALUACION.name(),
                "En evaluacion", "El postulante rindio la prueba pero no alcanzo el umbral de aprobacion.",
                2, false, true, ahora - MILISEGUNDOS_POR_DIA * 60));
        estadoRepository.guardar(new Estado("e3", EstadoCodigo.APROBADO_TECNICO.name(),
                "Aprobado tecnico", "Aprobo la evaluacion tecnica y avanza a entrevista.",
                3, false, true, ahora - MILISEGUNDOS_POR_DIA * 60));
        estadoRepository.guardar(new Estado("e4", EstadoCodigo.ENTREVISTA.name(),
                "Entrevista", "Citado o citada para entrevista personal.",
                4, false, true, ahora - MILISEGUNDOS_POR_DIA * 60));
        estadoRepository.guardar(new Estado("e5", EstadoCodigo.EVALUACION_PSICOLOGICA.name(),
                "Evaluacion psicologica", "Aprobada la entrevista, paso a evaluacion psicologica.",
                5, false, true, ahora - MILISEGUNDOS_POR_DIA * 60));
        estadoRepository.guardar(new Estado("e6", EstadoCodigo.ACEPTADO.name(),
                "Aceptado", "Proceso completo. Candidato aceptado para la posicion.",
                6, true, true, ahora - MILISEGUNDOS_POR_DIA * 60));
        estadoRepository.guardar(new Estado("e7", EstadoCodigo.RECHAZADO.name(),
                "Rechazado", "Candidato descartado en alguna etapa del proceso.",
                7, true, true, ahora - MILISEGUNDOS_POR_DIA * 60));
    }

    /* =================== USUARIOS =================== */
    private void cargarUsuarios(long ahora) {
        String idRolAdmin       = "r1";
        String idRolPostulante  = "r2";
        usuarioRepository.save(new Usuario("u1", "Administrador", "Continental", "admin@contitalent.com", "admin123",  idRolAdmin,      true, ahora - MILISEGUNDOS_POR_DIA * 30));
        usuarioRepository.save(new Usuario("u2", "Lucia",         "Ramos",       "lucia@example.com",     "lucia123",  idRolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 8));
        usuarioRepository.save(new Usuario("u3", "Carlos",        "Mendoza",     "carlos@example.com",    "carlos123", idRolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 5));
        usuarioRepository.save(new Usuario("u4", "Maria",         "Torres",      "maria@example.com",     "maria123",  idRolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 2));
        usuarioRepository.save(new Usuario("u5", "Pedro",         "Salinas",     "pedro@example.com",     "pedro123",  idRolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 7));
        usuarioRepository.save(new Usuario("u6", "Andrea",        "Leon",        "andrea@example.com",    "andrea123", idRolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 10));
        usuarioRepository.save(new Usuario("u7", "Diego",         "Alvarez",     "diego@example.com",     "diego123",  idRolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 15));
        usuarioRepository.save(new Usuario("u8", "Fiorella",      "Rojas",       "fiorella@example.com",  "fiora123",  idRolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 8));
    }

    /* =================== AREAS =================== */
    private void cargarAreas() {
        areaRepository.save(new Area("a1", "Ingenieria",                 "Sistemas, civil, industrial, mecatronica y minas.",                                       "engineering", "#6366f1"));
        areaRepository.save(new Area("a2", "Ciencias de la Empresa",     "Administracion, contabilidad, marketing y negocios.",                                     "chart",       "#06b6d4"));
        areaRepository.save(new Area("a3", "Derecho",                    "Derecho corporativo, civil, penal y constitucional.",                                     "scale",       "#8b5cf6"));
        areaRepository.save(new Area("a4", "Humanidades",                "Comunicacion, psicologia y educacion.",                                                   "books",       "#ec4899"));
        areaRepository.save(new Area("a5", "Ciencias de la Salud",       "Enfermeria, psicologia clinica y nutricion.",                                             "stethoscope", "#10b981"));
        areaRepository.save(new Area("a6", "Investigacion y Desarrollo", "Centros de investigacion e innovacion universitaria.",                                    "microscope",  "#f59e0b"));
        areaRepository.save(new Area("a7", "Tecnologia y Sistemas",      "Equipo de TI institucional: infraestructura, soporte y software interno.",                "computer",    "#0ea5e9"));
        areaRepository.save(new Area("a8", "Bienestar Universitario",    "Soporte estudiantil, deportes, cultura y atencion psicopedagogica.",                      "handshake",   "#f43f5e"));
    }

    /* =================== OFERTAS =================== */
    private void cargarOfertas(long ahora) {
        ofertaRepository.save(new Oferta("o1",  "Profesor de Programacion I",                "Trabajo",  "a1", "Presencial", "Huancayo", 2, true,
                "Docente para el curso de Programacion I (Java) en la Escuela de Ingenieria de Sistemas.",
                List.of("Ingeniero de Sistemas o afin", "2+ anios enseniando o desarrollando software", "Manejo de Java y bases de datos", "Experiencia en metodologias agiles"),
                List.of("Carga horaria flexible", "Capacitacion pedagogica", "Convenios interinstitucionales"),
                ahora - MILISEGUNDOS_POR_DIA * 3));
        ofertaRepository.save(new Oferta("o2",  "Practica Pre-Profesional Sistemas",         "Practica", "a1", "Hibrido",    "Huancayo", 4, true,
                "Apoyo al area de Sistemas y TI: soporte, desarrollo de pequenias mejoras y gestion de tickets.",
                List.of("Estudiante de Ingenieria de Sistemas", "Conocimientos de HTML, CSS y JS", "Buena comunicacion"),
                List.of("Subvencion economica", "Mentoria", "Certificacion de practicas"),
                ahora - MILISEGUNDOS_POR_DIA * 2));
        ofertaRepository.save(new Oferta("o3",  "Profesor de Marketing Digital",             "Trabajo",  "a2", "Presencial", "Huancayo", 1, true,
                "Docente para Marketing Digital y Estrategia Comercial.",
                List.of("Profesional en Marketing o Negocios", "Experiencia en performance digital", "Maestria (deseable)"),
                List.of("Plan de carrera docente", "Investigacion remunerada"),
                ahora - MILISEGUNDOS_POR_DIA * 4));
        ofertaRepository.save(new Oferta("o4",  "Practica Pre-Profesional Marketing",        "Practica", "a2", "Presencial", "Huancayo", 3, true,
                "Apoyo en campanias digitales, contenidos y analitica para la marca Continental.",
                List.of("Estudiante de Marketing/Administracion", "Manejo de redes sociales", "Curiosidad y proactividad"),
                List.of("Subvencion economica", "Aprendizaje real", "Certificacion"),
                ahora - MILISEGUNDOS_POR_DIA));
        ofertaRepository.save(new Oferta("o5",  "Profesor de Derecho Constitucional",        "Trabajo",  "a3", "Presencial", "Huancayo", 1, false,
                "Docente para el curso de Derecho Constitucional.",
                List.of("Abogado titulado", "Maestria o doctorado en Derecho", "Publicaciones academicas"),
                List.of("Bonos por publicacion", "Apoyo a investigacion"),
                ahora - MILISEGUNDOS_POR_DIA * 6));
        ofertaRepository.save(new Oferta("o6",  "Practica Profesional Derecho Civil",        "Practica", "a3", "Hibrido",    "Huancayo", 2, false,
                "Apoyo al consultorio juridico del area de Derecho.",
                List.of("Egresado o bachiller en Derecho", "Buen manejo de redaccion"),
                List.of("Subvencion", "Acompaniamiento profesional"),
                ahora - MILISEGUNDOS_POR_DIA * 5));
        ofertaRepository.save(new Oferta("o7",  "Profesor de Comunicacion Oral y Escrita",   "Trabajo",  "a4", "Presencial", "Huancayo", 2, false,
                "Curso transversal en pregrado.",
                List.of("Licenciado en Comunicacion o Educacion", "Experiencia minima 2 anios"),
                List.of("Plan docente", "Becas para postgrado"),
                ahora - MILISEGUNDOS_POR_DIA * 8));
        ofertaRepository.save(new Oferta("o8",  "Asistente de Investigacion Salud Publica",  "Practica", "a5", "Hibrido",    "Huancayo", 2, false,
                "Apoyo a investigacion de campo en proyectos de salud publica.",
                List.of("Estudiante de Ciencias de la Salud", "Estadistica basica"),
                List.of("Subvencion", "Co-autoria en publicaciones"),
                ahora - MILISEGUNDOS_POR_DIA * 9));
        ofertaRepository.save(new Oferta("o9",  "Coordinador de Investigacion",              "Trabajo",  "a6", "Presencial", "Huancayo", 1, false,
                "Lidera proyectos del Centro de Investigacion.",
                List.of("Magister o doctor", "Publicaciones indexadas", "Liderazgo de equipos"),
                List.of("Sueldo competitivo", "Asignacion de proyectos"),
                ahora - MILISEGUNDOS_POR_DIA * 11));
        ofertaRepository.save(new Oferta("o10", "Practica Soporte de TI Universitario",      "Practica", "a7", "Presencial", "Huancayo", 3, true,
                "Apoyo al equipo institucional de Tecnologia y Sistemas.",
                List.of("Estudios tecnicos o universitarios en TI", "Conocimientos de redes y hardware", "Buena atencion al usuario"),
                List.of("Subvencion economica", "Certificacion de practicas", "Plan de mentoria"),
                ahora - MILISEGUNDOS_POR_DIA));
        ofertaRepository.save(new Oferta("o11", "Coordinador de Bienestar Estudiantil",      "Trabajo",  "a8", "Presencial", "Huancayo", 1, false,
                "Lidera el equipo de Bienestar Universitario.",
                List.of("Profesional en Psicologia, Educacion o afin", "3+ anios en gestion estudiantil", "Habilidades de liderazgo"),
                List.of("Plan de carrera", "Capacitacion continua", "Contrato estable"),
                ahora - MILISEGUNDOS_POR_DIA * 4));
    }

    /* =================== PREGUNTAS =================== */
    private void cargarPreguntas() {
        preguntaRepository.save(new Pregunta("q1", "o1", "Que patron de diseno desacopla logica de presentacion?",
                List.of("Singleton", "MVC", "Observer", "Factory"), 1));
        preguntaRepository.save(new Pregunta("q2", "o1", "Cual es la principal ventaja de la inyeccion de dependencias?",
                List.of("Mejor rendimiento en runtime", "Tests mas sencillos y bajo acoplamiento", "Reduce el bundle", "Reemplaza interfaces"), 1));
        preguntaRepository.save(new Pregunta("q3", "o1", "Que hace una sentencia INNER JOIN en SQL?",
                List.of("Retorna todas las filas de ambas tablas", "Retorna filas con coincidencia en ambas tablas", "Retorna filas unicas", "Combina filas evitando duplicados"), 1));
        preguntaRepository.save(new Pregunta("q4", "o1", "Que metodologia activa promueve aprender por proyectos?",
                List.of("Conferencia magistral", "Aprendizaje basado en proyectos", "Examen oral", "Tutoriales pre-grabados"), 1));
        preguntaRepository.save(new Pregunta("q5", "o1", "Cual NO es un metodo HTTP idempotente?",
                List.of("GET", "PUT", "DELETE", "POST"), 3));

        preguntaRepository.save(new Pregunta("q6", "o2", "Que etiqueta HTML5 representa contenido principal?",
                List.of("<section>", "<main>", "<article>", "<div>"), 1));
        preguntaRepository.save(new Pregunta("q7", "o2", "Que propiedad CSS alinea elementos en eje horizontal con flex?",
                List.of("align-items", "justify-content", "flex-wrap", "grid-template"), 1));
        preguntaRepository.save(new Pregunta("q8", "o2", "Buena practica para evitar XSS?",
                List.of("Concatenar HTML con datos del usuario", "Escapar la salida y validar entradas", "Usar localStorage", "Cifrar las URLs"), 1));

        preguntaRepository.save(new Pregunta("q9",  "o3", "Que metrica mide el costo de adquirir un cliente?",
                List.of("CTR", "CAC", "ROI", "CPM"), 1));
        preguntaRepository.save(new Pregunta("q10", "o3", "Cual es el proposito principal del SEO tecnico?",
                List.of("Crear contenido viral", "Optimizar la indexacion y rendimiento", "Comprar enlaces", "Disenar logos"), 1));
        preguntaRepository.save(new Pregunta("q11", "o3", "En un funnel, que etapa va antes de la decision?",
                List.of("Awareness", "Consideration", "Retention", "Loyalty"), 1));

        preguntaRepository.save(new Pregunta("q12", "o4", "Herramienta estandar para piezas de redes sociales?",
                List.of("Figma", "Photoshop", "Excel", "Notepad"), 0));
        preguntaRepository.save(new Pregunta("q13", "o4", "Red social con mayor alcance B2B en LATAM?",
                List.of("Pinterest", "LinkedIn", "TikTok", "Snapchat"), 1));
    }

    /* =================== POSTULANTES =================== */
    private void cargarPostulantes(long ahora) {
        postulanteRepository.save(crearPostulante("p1", "u2", "o2", "Lucia Ramos", "lucia@example.com", "+51 987 654 321",
                "3 anios apoyando areas de TI en universidades", "JavaScript, soporte tecnico, atencion al usuario",
                "lucia_cv.pdf", "e2", 67, ahora - MILISEGUNDOS_POR_DIA * 4,
                Map.of("q6", 1, "q7", 1, "q8", 0)));
        postulanteRepository.save(crearPostulante("p2", "u3", "o1", "Carlos Mendoza", "carlos@example.com", "+51 911 222 333",
                "5 anios en arquitectura de software y docencia", "Java, Spring, Docker, didactica universitaria",
                "carlos_cv.pdf", "e3", 100, ahora - MILISEGUNDOS_POR_DIA * 3,
                Map.of("q1", 1, "q2", 1, "q3", 1, "q4", 1, "q5", 3)));
        postulanteRepository.save(crearPostulante("p3", "u4", "o4", "Maria Torres", "maria@example.com", "+51 933 555 777",
                "Estudiante de Marketing en ultimo ciclo", "Community management, creacion de contenido",
                "maria_cv.pdf", "e1", 0, ahora - MILISEGUNDOS_POR_DIA, Map.of()));
        postulanteRepository.save(crearPostulante("p4", "u5", "o2", "Pedro Salinas", "pedro@example.com", "+51 922 444 666",
                "Estudiante de Sistemas con practicas previas", "JavaScript, HTML, CSS, soporte",
                "pedro_cv.pdf", "e4", 100, ahora - MILISEGUNDOS_POR_DIA * 6,
                Map.of("q6", 1, "q7", 1, "q8", 1)));
        postulanteRepository.save(crearPostulante("p5", "u6", "o3", "Andrea Leon", "andrea@example.com", "+51 944 888 111",
                "6 anios en marketing B2B y docencia universitaria", "Estrategia digital, didactica, public speaking",
                "andrea_cv.pdf", "e5", 100, ahora - MILISEGUNDOS_POR_DIA * 9,
                Map.of("q9", 1, "q10", 1, "q11", 1)));
        postulanteRepository.save(crearPostulante("p6", "u7", "o3", "Diego Alvarez", "diego@example.com", "+51 955 777 222",
                "8 anios liderando agencias de marketing digital", "Ads, SEO, analitica, formacion de equipos",
                "diego_cv.pdf", "e6", 100, ahora - MILISEGUNDOS_POR_DIA * 14,
                Map.of("q9", 1, "q10", 1, "q11", 1)));
        postulanteRepository.save(crearPostulante("p7", "u8", "o4", "Fiorella Rojas", "fiorella@example.com", "+51 966 333 444",
                "1 anio en diseno grafico", "Figma, Illustrator",
                "fiorella_cv.pdf", "e7", 50, ahora - MILISEGUNDOS_POR_DIA * 7,
                Map.of("q12", 0, "q13", 0)));
    }

    private Postulante crearPostulante(String id, String usuarioId, String ofertaId, String nombre, String email,
                                       String telefono, String experiencia, String habilidades, String cv,
                                       String estadoId, int puntaje, long creadoEn,
                                       Map<String, Integer> respuestas) {
        Postulante postulante = new Postulante();
        postulante.setId(id);
        postulante.setUsuarioId(usuarioId);
        postulante.setOfertaId(ofertaId);
        postulante.setNombre(nombre);
        postulante.setEmail(email);
        postulante.setTelefono(telefono);
        postulante.setExperiencia(experiencia);
        postulante.setHabilidades(habilidades);
        postulante.setCv(cv);
        postulante.setEstadoId(estadoId);
        postulante.setPuntaje(puntaje);
        postulante.setRespuestas(new HashMap<>(respuestas));
        postulante.setCreadoEn(creadoEn);
        return postulante;
    }

    /* =================== METRICAS =================== */
    private void cargarMetricas() {
        MetricasDTO metricas = new MetricasDTO();
        Map<String, MetricasDTO.Serie> series = new LinkedHashMap<>();

        series.put("postulaciones", new MetricasDTO.Serie(
                "Postulaciones recibidas (ultimos 8 meses)", "postulantes",
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
                "Tiempo promedio de cierre (dias)", "dias",
                List.of(
                        new MetricasDTO.Punto("sep-25", 28), new MetricasDTO.Punto("oct-25", 26),
                        new MetricasDTO.Punto("nov-25", 27), new MetricasDTO.Punto("dic-25", 24),
                        new MetricasDTO.Punto("ene-26", 22), new MetricasDTO.Punto("feb-26", 23),
                        new MetricasDTO.Punto("mar-26", 21), new MetricasDTO.Punto("abr-26", 19))));

        series.put("puntajePromedio", new MetricasDTO.Serie(
                "Puntaje promedio en evaluacion tecnica", "pts",
                List.of(
                        new MetricasDTO.Punto("sep-25", 70), new MetricasDTO.Punto("oct-25", 72),
                        new MetricasDTO.Punto("nov-25", 71), new MetricasDTO.Punto("dic-25", 74),
                        new MetricasDTO.Punto("ene-26", 76), new MetricasDTO.Punto("feb-26", 75),
                        new MetricasDTO.Punto("mar-26", 78), new MetricasDTO.Punto("abr-26", 81))));

        series.put("tasaAceptacion", new MetricasDTO.Serie(
                "Tasa de aceptacion (%)", "%",
                List.of(
                        new MetricasDTO.Punto("sep-25", 9.6), new MetricasDTO.Punto("oct-25", 12.6),
                        new MetricasDTO.Punto("nov-25", 10.6), new MetricasDTO.Punto("dic-25", 14.1),
                        new MetricasDTO.Punto("ene-26", 15.4), new MetricasDTO.Punto("feb-26", 12.8),
                        new MetricasDTO.Punto("mar-26", 16.4), new MetricasDTO.Punto("abr-26", 16.9))));

        metricas.setSeries(series);

        MetricasDTO.EstadoActual estadoActual = new MetricasDTO.EstadoActual();
        estadoActual.setPostulantesActivos(183);
        estadoActual.setOfertasAbiertas(12);
        estadoActual.setEntrevistasHoy(7);
        estadoActual.setOfertasEsteMes(31);
        estadoActual.setTiempoPromedio("19 dias");
        metricas.setEstadoActual(estadoActual);

        metricasRepository.save(metricas);
    }
}
