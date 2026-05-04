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
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Carga inicial de datos en memoria. Solo se ejecuta si el repositorio de
 * usuarios esta vacio (seed idempotente).
 *
 * Orden de carga:
 *   1. Roles      (catalogo)
 *   2. Estados    (catalogo)
 *   3. Usuarios   (FK a Rol)
 *   4. Areas
 *   5. Ofertas    (FK a Area)
 *   6. Preguntas  (FK a Oferta)
 *   7. Postulantes (FK a Usuario, Oferta, Estado)
 *   8. Metricas   (snapshot en memoria)
 *
 * Los IDs se generan automaticamente por JPA (auto-increment). Las relaciones
 * se establecen con setters tipados que reciben las entidades resueltas.
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
    private final IDocumentoPostulanteRepository documentoRepository;
    private final IMetricasRepository metricasRepository;

    public DataLoader(IRolRepository rolRepository,
                      IEstadoRepository estadoRepository,
                      IUsuarioRepository usuarioRepository,
                      IAreaRepository areaRepository,
                      IOfertaRepository ofertaRepository,
                      IPreguntaRepository preguntaRepository,
                      IPostulanteRepository postulanteRepository,
                      IDocumentoPostulanteRepository documentoRepository,
                      IMetricasRepository metricasRepository) {
        this.rolRepository = rolRepository;
        this.estadoRepository = estadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.areaRepository = areaRepository;
        this.ofertaRepository = ofertaRepository;
        this.preguntaRepository = preguntaRepository;
        this.postulanteRepository = postulanteRepository;
        this.documentoRepository = documentoRepository;
        this.metricasRepository = metricasRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Las metricas (snapshot en memoria) se cargan siempre.
        cargarMetricas();

        if (usuarioRepository.count() > 0) {
            log.info("[DataLoader] Datos ya cargados en memoria; omito seed de entidades.");
            return;
        }
        log.info("[DataLoader] Cargando seed inicial de Conti Talent en memoria...");

        long ahora = System.currentTimeMillis();

        Map<String, Rol> rolesPorCodigo = cargarRoles(ahora);
        Map<String, Estado> estadosPorCodigo = cargarEstados(ahora);

        List<Usuario> usuarios = cargarUsuarios(ahora, rolesPorCodigo);
        Map<String, Area> areasPorNombre = cargarAreas();
        Map<String, Oferta> ofertasPorTitulo = cargarOfertas(ahora, areasPorNombre);
        Map<String, Pregunta> preguntasPorClave = cargarPreguntas(ofertasPorTitulo);
        cargarPostulantes(ahora, usuarios, ofertasPorTitulo, estadosPorCodigo, preguntasPorClave);

        log.info("[DataLoader] Seed completado: {} roles, {} estados, {} usuarios, {} areas, {} ofertas, {} preguntas, {} postulantes",
                rolRepository.count(),
                estadoRepository.count(),
                usuarioRepository.count(),
                areaRepository.count(),
                ofertaRepository.count(),
                preguntaRepository.count(),
                postulanteRepository.count());
    }

    /* =================== ROLES =================== */
    private Map<String, Rol> cargarRoles(long ahora) {
        Map<String, Rol> mapa = new HashMap<>();
        mapa.put(RolCodigo.ADMIN.name(), rolRepository.save(new Rol(
                RolCodigo.ADMIN.name(), "Administrador",
                "Acceso total al sistema: gestion de usuarios, areas, ofertas, postulantes y metricas.",
                true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        mapa.put(RolCodigo.POSTULANTE.name(), rolRepository.save(new Rol(
                RolCodigo.POSTULANTE.name(), "Postulante",
                "Usuario externo que postula a las ofertas publicadas por la institucion.",
                true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        return mapa;
    }

    /* =================== ESTADOS =================== */
    private Map<String, Estado> cargarEstados(long ahora) {
        Map<String, Estado> mapa = new LinkedHashMap<>();
        mapa.put(EstadoCodigo.POSTULADO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.POSTULADO.name(), "Postulado",
                "Postulacion recibida y pendiente de evaluacion tecnica.",
                1, false, true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        mapa.put(EstadoCodigo.EN_EVALUACION.name(), estadoRepository.save(new Estado(
                EstadoCodigo.EN_EVALUACION.name(), "En evaluacion",
                "El postulante rindio la prueba pero no alcanzo el umbral de aprobacion.",
                2, false, true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        mapa.put(EstadoCodigo.APROBADO_TECNICO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.APROBADO_TECNICO.name(), "Aprobado tecnico",
                "Aprobo la evaluacion tecnica y avanza a entrevista.",
                3, false, true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        mapa.put(EstadoCodigo.ENTREVISTA.name(), estadoRepository.save(new Estado(
                EstadoCodigo.ENTREVISTA.name(), "Entrevista",
                "Citado o citada para entrevista personal.",
                4, false, true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        mapa.put(EstadoCodigo.EVALUACION_PSICOLOGICA.name(), estadoRepository.save(new Estado(
                EstadoCodigo.EVALUACION_PSICOLOGICA.name(), "Evaluacion psicologica",
                "Aprobada la entrevista, paso a evaluacion psicologica.",
                5, false, true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        mapa.put(EstadoCodigo.ACEPTADO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.ACEPTADO.name(), "Aceptado",
                "Proceso completo. Candidato aceptado para la posicion.",
                6, true, true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        mapa.put(EstadoCodigo.RECHAZADO.name(), estadoRepository.save(new Estado(
                EstadoCodigo.RECHAZADO.name(), "Rechazado",
                "Candidato descartado en alguna etapa del proceso.",
                7, true, true, ahora - MILISEGUNDOS_POR_DIA * 60)));
        return mapa;
    }

    /* =================== USUARIOS =================== */
    private List<Usuario> cargarUsuarios(long ahora, Map<String, Rol> rolesPorCodigo) {
        Rol rolAdmin       = rolesPorCodigo.get(RolCodigo.ADMIN.name());
        Rol rolPostulante  = rolesPorCodigo.get(RolCodigo.POSTULANTE.name());

        List<Usuario> creados = new ArrayList<>();
        creados.add(usuarioRepository.save(new Usuario("Administrador", "Continental", "admin@contitalent.com", "admin123", rolAdmin,      true, ahora - MILISEGUNDOS_POR_DIA * 30)));
        creados.add(usuarioRepository.save(new Usuario("Lucia",         "Ramos",       "lucia@example.com",     "lucia123",  rolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 8)));
        creados.add(usuarioRepository.save(new Usuario("Carlos",        "Mendoza",     "carlos@example.com",    "carlos123", rolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 5)));
        creados.add(usuarioRepository.save(new Usuario("Maria",         "Torres",      "maria@example.com",     "maria123",  rolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 2)));
        creados.add(usuarioRepository.save(new Usuario("Pedro",         "Salinas",     "pedro@example.com",     "pedro123",  rolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 7)));
        creados.add(usuarioRepository.save(new Usuario("Andrea",        "Leon",        "andrea@example.com",    "andrea123", rolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 10)));
        creados.add(usuarioRepository.save(new Usuario("Diego",         "Alvarez",     "diego@example.com",     "diego123",  rolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 15)));
        creados.add(usuarioRepository.save(new Usuario("Fiorella",      "Rojas",       "fiorella@example.com",  "fiora123",  rolPostulante, true, ahora - MILISEGUNDOS_POR_DIA * 8)));
        return creados;
    }

    /* =================== AREAS =================== */
    private Map<String, Area> cargarAreas() {
        Map<String, Area> mapa = new LinkedHashMap<>();
        mapa.put("Ingenieria",                 areaRepository.save(new Area("Ingenieria",                 "Sistemas, civil, industrial, mecatronica y minas.",                                       "engineering", "#6366f1")));
        mapa.put("Ciencias de la Empresa",     areaRepository.save(new Area("Ciencias de la Empresa",     "Administracion, contabilidad, marketing y negocios.",                                     "chart",       "#06b6d4")));
        mapa.put("Derecho",                    areaRepository.save(new Area("Derecho",                    "Derecho corporativo, civil, penal y constitucional.",                                     "scale",       "#8b5cf6")));
        mapa.put("Humanidades",                areaRepository.save(new Area("Humanidades",                "Comunicacion, psicologia y educacion.",                                                   "books",       "#ec4899")));
        mapa.put("Ciencias de la Salud",       areaRepository.save(new Area("Ciencias de la Salud",       "Enfermeria, psicologia clinica y nutricion.",                                             "stethoscope", "#10b981")));
        mapa.put("Investigacion y Desarrollo", areaRepository.save(new Area("Investigacion y Desarrollo", "Centros de investigacion e innovacion universitaria.",                                    "microscope",  "#f59e0b")));
        mapa.put("Tecnologia y Sistemas",      areaRepository.save(new Area("Tecnologia y Sistemas",      "Equipo de TI institucional: infraestructura, soporte y software interno.",                "computer",    "#0ea5e9")));
        mapa.put("Bienestar Universitario",    areaRepository.save(new Area("Bienestar Universitario",    "Soporte estudiantil, deportes, cultura y atencion psicopedagogica.",                      "handshake",   "#f43f5e")));
        return mapa;
    }

    /* =================== OFERTAS =================== */
    private Map<String, Oferta> cargarOfertas(long ahora, Map<String, Area> areas) {
        Map<String, Oferta> mapa = new LinkedHashMap<>();

        mapa.put("Profesor de Programacion I", ofertaRepository.save(new Oferta(
                "Profesor de Programacion I", "Trabajo", areas.get("Ingenieria"),
                "Presencial", "Huancayo", 2, true,
                "Docente para el curso de Programacion I (Java) en la Escuela de Ingenieria de Sistemas.",
                List.of("Ingeniero de Sistemas o afin", "2+ anios enseniando o desarrollando software", "Manejo de Java y bases de datos", "Experiencia en metodologias agiles"),
                List.of("Carga horaria flexible", "Capacitacion pedagogica", "Convenios interinstitucionales"),
                ahora - MILISEGUNDOS_POR_DIA * 3)));
        mapa.get("Profesor de Programacion I").setHorario("Lunes, miercoles y viernes 08:00 - 12:00");

        mapa.put("Practica Sistemas", ofertaRepository.save(new Oferta(
                "Practica Pre-Profesional Sistemas", "Practica", areas.get("Ingenieria"),
                "Hibrido", "Huancayo", 4, true,
                "Apoyo al area de Sistemas y TI: soporte, desarrollo de pequenias mejoras y gestion de tickets.",
                List.of("Estudiante de Ingenieria de Sistemas", "Conocimientos de HTML, CSS y JS", "Buena comunicacion"),
                List.of("Subvencion economica", "Mentoria", "Certificacion de practicas"),
                ahora - MILISEGUNDOS_POR_DIA * 2)));
        mapa.get("Practica Sistemas").setHorario("Lunes a viernes 09:00 - 14:00");

        mapa.put("Profesor Marketing Digital", ofertaRepository.save(new Oferta(
                "Profesor de Marketing Digital", "Trabajo", areas.get("Ciencias de la Empresa"),
                "Presencial", "Huancayo", 1, true,
                "Docente para Marketing Digital y Estrategia Comercial.",
                List.of("Profesional en Marketing o Negocios", "Experiencia en performance digital", "Maestria (deseable)"),
                List.of("Plan de carrera docente", "Investigacion remunerada"),
                ahora - MILISEGUNDOS_POR_DIA * 4)));
        mapa.get("Profesor Marketing Digital").setHorario("Martes y jueves 18:00 - 21:00");

        mapa.put("Practica Marketing", ofertaRepository.save(new Oferta(
                "Practica Pre-Profesional Marketing", "Practica", areas.get("Ciencias de la Empresa"),
                "Presencial", "Huancayo", 3, true,
                "Apoyo en campanias digitales, contenidos y analitica para la marca Continental.",
                List.of("Estudiante de Marketing/Administracion", "Manejo de redes sociales", "Curiosidad y proactividad"),
                List.of("Subvencion economica", "Aprendizaje real", "Certificacion"),
                ahora - MILISEGUNDOS_POR_DIA)));
        mapa.get("Practica Marketing").setHorario("Lunes a viernes 08:30 - 13:30");

        mapa.put("Profesor Derecho Constitucional", ofertaRepository.save(new Oferta(
                "Profesor de Derecho Constitucional", "Trabajo", areas.get("Derecho"),
                "Presencial", "Huancayo", 1, false,
                "Docente para el curso de Derecho Constitucional.",
                List.of("Abogado titulado", "Maestria o doctorado en Derecho", "Publicaciones academicas"),
                List.of("Bonos por publicacion", "Apoyo a investigacion"),
                ahora - MILISEGUNDOS_POR_DIA * 6)));
        mapa.get("Profesor Derecho Constitucional").setHorario("Sabados 08:00 - 13:00");

        mapa.put("Practica Derecho Civil", ofertaRepository.save(new Oferta(
                "Practica Profesional Derecho Civil", "Practica", areas.get("Derecho"),
                "Hibrido", "Huancayo", 2, false,
                "Apoyo al consultorio juridico del area de Derecho.",
                List.of("Egresado o bachiller en Derecho", "Buen manejo de redaccion"),
                List.of("Subvencion", "Acompaniamiento profesional"),
                ahora - MILISEGUNDOS_POR_DIA * 5)));
        mapa.get("Practica Derecho Civil").setHorario("Lunes a viernes 09:00 - 15:00");

        mapa.put("Profesor Comunicacion", ofertaRepository.save(new Oferta(
                "Profesor de Comunicacion Oral y Escrita", "Trabajo", areas.get("Humanidades"),
                "Presencial", "Huancayo", 2, false,
                "Curso transversal en pregrado.",
                List.of("Licenciado en Comunicacion o Educacion", "Experiencia minima 2 anios"),
                List.of("Plan docente", "Becas para postgrado"),
                ahora - MILISEGUNDOS_POR_DIA * 8)));
        mapa.get("Profesor Comunicacion").setHorario("Lunes y miercoles 16:00 - 20:00");

        mapa.put("Asistente Salud Publica", ofertaRepository.save(new Oferta(
                "Asistente de Investigacion Salud Publica", "Practica", areas.get("Ciencias de la Salud"),
                "Hibrido", "Huancayo", 2, false,
                "Apoyo a investigacion de campo en proyectos de salud publica.",
                List.of("Estudiante de Ciencias de la Salud", "Estadistica basica"),
                List.of("Subvencion", "Co-autoria en publicaciones"),
                ahora - MILISEGUNDOS_POR_DIA * 9)));
        mapa.get("Asistente Salud Publica").setHorario("Lunes a viernes 08:00 - 13:00");

        mapa.put("Coordinador Investigacion", ofertaRepository.save(new Oferta(
                "Coordinador de Investigacion", "Trabajo", areas.get("Investigacion y Desarrollo"),
                "Presencial", "Huancayo", 1, false,
                "Lidera proyectos del Centro de Investigacion.",
                List.of("Magister o doctor", "Publicaciones indexadas", "Liderazgo de equipos"),
                List.of("Sueldo competitivo", "Asignacion de proyectos"),
                ahora - MILISEGUNDOS_POR_DIA * 11)));
        mapa.get("Coordinador Investigacion").setHorario("Lunes a viernes 08:00 - 17:00");

        mapa.put("Practica Soporte TI", ofertaRepository.save(new Oferta(
                "Practica Soporte de TI Universitario", "Practica", areas.get("Tecnologia y Sistemas"),
                "Presencial", "Huancayo", 3, true,
                "Apoyo al equipo institucional de Tecnologia y Sistemas.",
                List.of("Estudios tecnicos o universitarios en TI", "Conocimientos de redes y hardware", "Buena atencion al usuario"),
                List.of("Subvencion economica", "Certificacion de practicas", "Plan de mentoria"),
                ahora - MILISEGUNDOS_POR_DIA)));
        mapa.get("Practica Soporte TI").setHorario("Turno maniana 08:00 - 13:00");

        mapa.put("Coordinador Bienestar", ofertaRepository.save(new Oferta(
                "Coordinador de Bienestar Estudiantil", "Trabajo", areas.get("Bienestar Universitario"),
                "Presencial", "Huancayo", 1, false,
                "Lidera el equipo de Bienestar Universitario.",
                List.of("Profesional en Psicologia, Educacion o afin", "3+ anios en gestion estudiantil", "Habilidades de liderazgo"),
                List.of("Plan de carrera", "Capacitacion continua", "Contrato estable"),
                ahora - MILISEGUNDOS_POR_DIA * 4)));
        mapa.get("Coordinador Bienestar").setHorario("Lunes a viernes 08:30 - 17:30");

        mapa.get("Profesor de Programacion I").setHabilidadesRequeridas(List.of("Java", "bases de datos", "metodologias agiles", "didactica"));
        mapa.get("Practica Sistemas").setHabilidadesRequeridas(List.of("HTML", "CSS", "JavaScript", "soporte tecnico"));
        mapa.get("Profesor Marketing Digital").setHabilidadesRequeridas(List.of("SEO", "Ads", "analitica", "didactica"));
        mapa.get("Practica Marketing").setHabilidadesRequeridas(List.of("redes sociales", "contenido", "Figma"));
        mapa.get("Profesor Derecho Constitucional").setHabilidadesRequeridas(List.of("Derecho", "publicaciones academicas", "docencia"));
        mapa.get("Practica Derecho Civil").setHabilidadesRequeridas(List.of("redaccion", "Derecho civil"));
        mapa.get("Profesor Comunicacion").setHabilidadesRequeridas(List.of("comunicacion", "educacion", "docencia"));
        mapa.get("Asistente Salud Publica").setHabilidadesRequeridas(List.of("estadistica", "investigacion"));
        mapa.get("Coordinador Investigacion").setHabilidadesRequeridas(List.of("publicaciones indexadas", "liderazgo", "investigacion"));
        mapa.get("Practica Soporte TI").setHabilidadesRequeridas(List.of("redes", "hardware", "atencion al usuario"));
        mapa.get("Coordinador Bienestar").setHabilidadesRequeridas(List.of("liderazgo", "gestion estudiantil", "psicologia"));
        mapa.values().forEach(ofertaRepository::save);

        return mapa;
    }

    /* =================== PREGUNTAS =================== */
    private Map<String, Pregunta> cargarPreguntas(Map<String, Oferta> ofertas) {
        Map<String, Pregunta> mapa = new LinkedHashMap<>();
        Oferta progI = ofertas.get("Profesor de Programacion I");
        Oferta sistemas = ofertas.get("Practica Sistemas");
        Oferta marketing = ofertas.get("Profesor Marketing Digital");
        Oferta marketingPrac = ofertas.get("Practica Marketing");

        mapa.put("q1", preguntaRepository.save(new Pregunta(progI,
                "Que patron de diseno desacopla logica de presentacion?",
                List.of("Singleton", "MVC", "Observer", "Factory"), 1)));
        mapa.put("q2", preguntaRepository.save(new Pregunta(progI,
                "Cual es la principal ventaja de la inyeccion de dependencias?",
                List.of("Mejor rendimiento en runtime", "Tests mas sencillos y bajo acoplamiento", "Reduce el bundle", "Reemplaza interfaces"), 1)));
        mapa.put("q3", preguntaRepository.save(new Pregunta(progI,
                "Que hace una sentencia INNER JOIN en SQL?",
                List.of("Retorna todas las filas de ambas tablas", "Retorna filas con coincidencia en ambas tablas", "Retorna filas unicas", "Combina filas evitando duplicados"), 1)));
        mapa.put("q4", preguntaRepository.save(new Pregunta(progI,
                "Que metodologia activa promueve aprender por proyectos?",
                List.of("Conferencia magistral", "Aprendizaje basado en proyectos", "Examen oral", "Tutoriales pre-grabados"), 1)));
        mapa.put("q5", preguntaRepository.save(new Pregunta(progI,
                "Cual NO es un metodo HTTP idempotente?",
                List.of("GET", "PUT", "DELETE", "POST"), 3)));

        mapa.put("q6", preguntaRepository.save(new Pregunta(sistemas,
                "Que etiqueta HTML5 representa contenido principal?",
                List.of("<section>", "<main>", "<article>", "<div>"), 1)));
        mapa.put("q7", preguntaRepository.save(new Pregunta(sistemas,
                "Que propiedad CSS alinea elementos en eje horizontal con flex?",
                List.of("align-items", "justify-content", "flex-wrap", "grid-template"), 1)));
        mapa.put("q8", preguntaRepository.save(new Pregunta(sistemas,
                "Buena practica para evitar XSS?",
                List.of("Concatenar HTML con datos del usuario", "Escapar la salida y validar entradas", "Usar localStorage", "Cifrar las URLs"), 1)));

        mapa.put("q9",  preguntaRepository.save(new Pregunta(marketing,
                "Que metrica mide el costo de adquirir un cliente?",
                List.of("CTR", "CAC", "ROI", "CPM"), 1)));
        mapa.put("q10", preguntaRepository.save(new Pregunta(marketing,
                "Cual es el proposito principal del SEO tecnico?",
                List.of("Crear contenido viral", "Optimizar la indexacion y rendimiento", "Comprar enlaces", "Disenar logos"), 1)));
        mapa.put("q11", preguntaRepository.save(new Pregunta(marketing,
                "En un funnel, que etapa va antes de la decision?",
                List.of("Awareness", "Consideration", "Retention", "Loyalty"), 1)));

        mapa.put("q12", preguntaRepository.save(new Pregunta(marketingPrac,
                "Herramienta estandar para piezas de redes sociales?",
                List.of("Figma", "Photoshop", "Excel", "Notepad"), 0)));
        mapa.put("q13", preguntaRepository.save(new Pregunta(marketingPrac,
                "Red social con mayor alcance B2B en LATAM?",
                List.of("Pinterest", "LinkedIn", "TikTok", "Snapchat"), 1)));

        return mapa;
    }

    /* =================== POSTULANTES =================== */
    private void cargarPostulantes(long ahora, List<Usuario> usuarios,
                                   Map<String, Oferta> ofertas, Map<String, Estado> estados,
                                   Map<String, Pregunta> preguntas) {
        // Resolucion de los usuarios por su posicion en el seed (admin=0, postulantes 1..7).
        Usuario lucia    = usuarios.get(1);
        Usuario carlos   = usuarios.get(2);
        Usuario maria    = usuarios.get(3);
        Usuario pedro    = usuarios.get(4);
        Usuario andrea   = usuarios.get(5);
        Usuario diego    = usuarios.get(6);
        Usuario fiorella = usuarios.get(7);

        Postulante postulanteLucia = postulanteRepository.save(crearPostulante(lucia, ofertas.get("Practica Sistemas"),
                estados.get(EstadoCodigo.EN_EVALUACION.name()),
                "Lucia Ramos", "lucia@example.com", "+51 987 654 321",
                "3 anios apoyando areas de TI en universidades", "JavaScript, soporte tecnico, atencion al usuario",
                "lucia_cv.pdf", 67, ahora - MILISEGUNDOS_POR_DIA * 4,
                Map.of(preguntas.get("q6").getId(), 1, preguntas.get("q7").getId(), 1, preguntas.get("q8").getId(), 0)));

        Postulante postulanteCarlos = postulanteRepository.save(crearPostulante(carlos, ofertas.get("Profesor de Programacion I"),
                estados.get(EstadoCodigo.APROBADO_TECNICO.name()),
                "Carlos Mendoza", "carlos@example.com", "+51 911 222 333",
                "5 anios en arquitectura de software y docencia", "Java, Spring, Docker, didactica universitaria",
                "carlos_cv.pdf", 100, ahora - MILISEGUNDOS_POR_DIA * 3,
                Map.of(preguntas.get("q1").getId(), 1, preguntas.get("q2").getId(), 1,
                       preguntas.get("q3").getId(), 1, preguntas.get("q4").getId(), 1, preguntas.get("q5").getId(), 3)));

        postulanteRepository.save(crearPostulante(maria, ofertas.get("Practica Marketing"),
                estados.get(EstadoCodigo.POSTULADO.name()),
                "Maria Torres", "maria@example.com", "+51 933 555 777",
                "Estudiante de Marketing en ultimo ciclo", "Community management, creacion de contenido",
                "maria_cv.pdf", 0, ahora - MILISEGUNDOS_POR_DIA, Map.of()));

        Postulante postulantePedro = postulanteRepository.save(crearPostulante(pedro, ofertas.get("Practica Sistemas"),
                estados.get(EstadoCodigo.ENTREVISTA.name()),
                "Pedro Salinas", "pedro@example.com", "+51 922 444 666",
                "Estudiante de Sistemas con practicas previas", "JavaScript, HTML, CSS, soporte",
                "pedro_cv.pdf", 100, ahora - MILISEGUNDOS_POR_DIA * 6,
                Map.of(preguntas.get("q6").getId(), 1, preguntas.get("q7").getId(), 1, preguntas.get("q8").getId(), 1)));

        Postulante postulanteAndrea = postulanteRepository.save(crearPostulante(andrea, ofertas.get("Profesor Marketing Digital"),
                estados.get(EstadoCodigo.EVALUACION_PSICOLOGICA.name()),
                "Andrea Leon", "andrea@example.com", "+51 944 888 111",
                "6 anios en marketing B2B y docencia universitaria", "Estrategia digital, didactica, public speaking",
                "andrea_cv.pdf", 100, ahora - MILISEGUNDOS_POR_DIA * 9,
                Map.of(preguntas.get("q9").getId(), 1, preguntas.get("q10").getId(), 1, preguntas.get("q11").getId(), 1)));

        postulanteRepository.save(crearPostulante(diego, ofertas.get("Profesor Marketing Digital"),
                estados.get(EstadoCodigo.ACEPTADO.name()),
                "Diego Alvarez", "diego@example.com", "+51 955 777 222",
                "8 anios liderando agencias de marketing digital", "Ads, SEO, analitica, formacion de equipos",
                "diego_cv.pdf", 100, ahora - MILISEGUNDOS_POR_DIA * 14,
                Map.of(preguntas.get("q9").getId(), 1, preguntas.get("q10").getId(), 1, preguntas.get("q11").getId(), 1)));

        postulanteRepository.save(crearPostulante(fiorella, ofertas.get("Practica Marketing"),
                estados.get(EstadoCodigo.RECHAZADO.name()),
                "Fiorella Rojas", "fiorella@example.com", "+51 966 333 444",
                "1 anio en diseno grafico", "Figma, Illustrator",
                "fiorella_cv.pdf", 50, ahora - MILISEGUNDOS_POR_DIA * 7,
                Map.of(preguntas.get("q12").getId(), 0, preguntas.get("q13").getId(), 0)));

        cargarDocumentosSeed(ahora, postulanteLucia, postulanteCarlos, postulantePedro, postulanteAndrea);
    }

    private void cargarDocumentosSeed(long ahora, Postulante lucia, Postulante carlos, Postulante pedro, Postulante andrea) {
        crearDocumentoSeed(lucia, "CV", "lucia_cv.pdf", "lucia_cv.pdf", ahora - MILISEGUNDOS_POR_DIA * 4);
        crearDocumentoSeed(carlos, "CV", "carlos_cv.pdf", "carlos_cv.pdf", ahora - MILISEGUNDOS_POR_DIA * 3);
        crearDocumentoSeed(carlos, "CERTIFICADO", "certificado_spring.pdf", "certificado_spring.pdf", ahora - MILISEGUNDOS_POR_DIA * 3);
        crearDocumentoSeed(pedro, "CV", "pedro_cv.pdf", "pedro_cv.pdf", ahora - MILISEGUNDOS_POR_DIA * 6);
        crearDocumentoSeed(andrea, "CV", "andrea_cv.pdf", "andrea_cv.pdf", ahora - MILISEGUNDOS_POR_DIA * 9);
    }

    private void crearDocumentoSeed(Postulante postulante, String tipo, String nombreOriginal, String nombreArchivo, long fechaSubida) {
        Path ruta = Paths.get("uploads", nombreArchivo).toAbsolutePath().normalize();
        DocumentoPostulante documento = new DocumentoPostulante();
        documento.setPostulante(postulante);
        documento.setTipoDocumento(tipo);
        documento.setNombreOriginal(nombreOriginal);
        documento.setNombreArchivo(nombreArchivo);
        documento.setRutaArchivo(ruta.toString());
        documento.setExtension("pdf");
        try {
            documento.setTamanio(Files.exists(ruta) ? Files.size(ruta) : 0L);
        } catch (Exception ignored) {
            documento.setTamanio(0L);
        }
        documento.setFechaSubida(fechaSubida);
        documentoRepository.save(documento);
        postulante.getDocumentos().add(documento);
    }

    private Postulante crearPostulante(Usuario usuario, Oferta oferta, Estado estado,
                                       String nombre, String email, String telefono,
                                       String experiencia, String habilidades, String cv,
                                       int puntaje, long creadoEn,
                                       Map<Long, Integer> respuestas) {
        Postulante postulante = new Postulante();
        postulante.setUsuario(usuario);
        postulante.setOferta(oferta);
        postulante.setEstado(estado);
        postulante.setNombre(nombre);
        postulante.setEmail(email);
        postulante.setTelefono(telefono);
        postulante.setExperiencia(experiencia);
        postulante.setHabilidades(habilidades);
        postulante.setCv(cv);
        postulante.setPuntaje(puntaje);
        postulante.setFechaPostulacion(creadoEn);
        postulante.setFechaEvaluacion(respuestas != null && !respuestas.isEmpty() ? creadoEn + 3_600_000L : null);
        postulante.setAniosExperiencia(extraerAniosExperiencia(experiencia));
        postulante.setNivelEstudios("Universitario");
        postulante.setCarrera("No especificada");
        postulante.setDisponibilidad("Inmediata");
        postulante.setModalidadPreferida(oferta.getModalidad());
        postulante.setPuntajeExperiencia(calcularPuntajeExperiencia(postulante.getAniosExperiencia()));
        postulante.setPuntajeHabilidades(60);
        postulante.setPuntajeFinal((int) Math.round((puntaje * 0.50) + (postulante.getPuntajeExperiencia() * 0.30) + (postulante.getPuntajeHabilidades() * 0.20)));
        postulante.setRespuestas(new HashMap<>(respuestas));
        postulante.setCreadoEn(creadoEn);
        return postulante;
    }

    private int extraerAniosExperiencia(String experiencia) {
        if (experiencia == null) return 0;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)").matcher(experiencia);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private int calcularPuntajeExperiencia(int anios) {
        if (anios <= 0) return 0;
        if (anios == 1) return 30;
        if (anios == 2) return 60;
        return 100;
    }

    /* =================== METRICAS (snapshot en memoria) =================== */
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
