package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Estado;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoCodigo;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IEstadoRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPreguntaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEvaluacionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Logica de evaluacion tecnica del postulante:
 *  - Evita doble evaluacion (un postulante solo puede rendir una vez).
 *  - Calcula puntaje sobre 100.
 *  - Mueve estado automaticamente: APROBADO_TECNICO si puntaje >= 70,
 *    en caso contrario EN_EVALUACION.
 */
@Service
public class EvaluacionServiceImpl implements IEvaluacionService {

    private static final int UMBRAL_APROBACION = 70;

    private final IPostulanteRepository postulanteRepository;
    private final IPreguntaRepository preguntaRepository;
    private final IEstadoRepository estadoRepository;

    public EvaluacionServiceImpl(IPostulanteRepository postulanteRepository,
                                 IPreguntaRepository preguntaRepository,
                                 IEstadoRepository estadoRepository) {
        this.postulanteRepository = postulanteRepository;
        this.preguntaRepository = preguntaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public EvaluacionDTO calificar(EvaluacionRequest request) {
        validarRequest(request);
        Postulante postulante = buscarPostulanteOFallar(request.getPostulanteId());

        verificarQueNoEsteYaEvaluado(postulante);
        verificarQueEsteEnEtapaQuePermiteEvaluar(postulante);

        List<Pregunta> preguntasOferta = preguntaRepository.findByOfertaId(postulante.getOfertaId());
        Map<String, Integer> respuestasMarcadas = request.getRespuestas() != null
                ? request.getRespuestas()
                : new HashMap<>();

        int totalPreguntas    = preguntasOferta.size();
        int respuestasCorrectas = contarRespuestasCorrectas(preguntasOferta, respuestasMarcadas);
        int puntajeFinal      = calcularPuntajeSobre100(respuestasCorrectas, totalPreguntas);

        actualizarPostulanteConEvaluacion(postulante, puntajeFinal, respuestasMarcadas);

        return new EvaluacionDTO(
                postulante.getId(), postulante.getOfertaId(),
                puntajeFinal, respuestasCorrectas, totalPreguntas,
                respuestasMarcadas, System.currentTimeMillis());
    }

    @Override
    public EvaluacionDTO obtenerPorPostulante(String postulanteId) {
        Postulante postulante = buscarPostulanteOFallar(postulanteId);
        if (postulante.getRespuestas() == null || postulante.getRespuestas().isEmpty()) {
            throw new ResourceNotFoundException(
                    "El postulante no ha realizado una evaluacion todavia");
        }
        List<Pregunta> preguntasOferta = preguntaRepository.findByOfertaId(postulante.getOfertaId());
        int respuestasCorrectas = contarRespuestasCorrectas(preguntasOferta, postulante.getRespuestas());

        return new EvaluacionDTO(
                postulante.getId(), postulante.getOfertaId(), postulante.getPuntaje(),
                respuestasCorrectas, preguntasOferta.size(),
                postulante.getRespuestas(), 0L);
    }

    /* ============ Helpers privados ============ */

    private static void validarRequest(EvaluacionRequest request) {
        if (request == null || esTextoVacio(request.getPostulanteId())) {
            throw new BusinessException("postulanteId requerido");
        }
    }

    private Postulante buscarPostulanteOFallar(String id) {
        return postulanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Postulante no encontrado: " + id));
    }

    private void verificarQueNoEsteYaEvaluado(Postulante postulante) {
        if (postulante.getRespuestas() != null && !postulante.getRespuestas().isEmpty()) {
            throw new BusinessException("El postulante ya tiene una evaluacion registrada");
        }
    }

    private void verificarQueEsteEnEtapaQuePermiteEvaluar(Postulante postulante) {
        Estado estadoActual = estadoRepository.buscarPorId(postulante.getEstadoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado del postulante no encontrado: " + postulante.getEstadoId()));
        EstadoCodigo codigoActual = EstadoCodigo.valueOf(estadoActual.getCodigo());

        if (codigoActual != EstadoCodigo.POSTULADO && codigoActual != EstadoCodigo.EN_EVALUACION) {
            throw new BusinessException(
                    "El postulante no esta en una etapa que permita evaluar (estado actual: "
                            + codigoActual + ")");
        }
    }

    private static int contarRespuestasCorrectas(List<Pregunta> preguntas,
                                                 Map<String, Integer> respuestas) {
        int correctas = 0;
        for (Pregunta pregunta : preguntas) {
            Integer indiceMarcado = respuestas.get(pregunta.getId());
            if (indiceMarcado != null && indiceMarcado == pregunta.getCorrecta()) {
                correctas++;
            }
        }
        return correctas;
    }

    private static int calcularPuntajeSobre100(int correctas, int total) {
        if (total == 0) return 0;
        return (int) Math.round((correctas * 100.0) / total);
    }

    private void actualizarPostulanteConEvaluacion(Postulante postulante,
                                                   int puntajeFinal,
                                                   Map<String, Integer> respuestas) {
        postulante.setPuntaje(puntajeFinal);
        postulante.setRespuestas(new HashMap<>(respuestas));

        EstadoCodigo codigoSiguiente = puntajeFinal >= UMBRAL_APROBACION
                ? EstadoCodigo.APROBADO_TECNICO
                : EstadoCodigo.EN_EVALUACION;
        Estado estadoSiguiente = estadoRepository.buscarPorCodigo(codigoSiguiente.name())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estado '" + codigoSiguiente + "' no esta cargado en el sistema"));
        postulante.setEstadoId(estadoSiguiente.getId());

        postulanteRepository.save(postulante);
    }

    private static boolean esTextoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
