package com.conti_talent.springboot.appweb.conti_talent_web.service.impl;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionRequest;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.BusinessException;
import com.conti_talent.springboot.appweb.conti_talent_web.exception.ResourceNotFoundException;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Postulante;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Pregunta;
import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPostulanteRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.repository.IPreguntaRepository;
import com.conti_talent.springboot.appweb.conti_talent_web.service.IEvaluacionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lógica de evaluación técnica:
 *  - Evita doble evaluación.
 *  - Calcula puntaje sobre 100.
 *  - Mueve estado: APROBADO_TECNICO si >= 70, sino EN_EVALUACION.
 */
@Service
public class EvaluacionServiceImpl implements IEvaluacionService {

    private static final int UMBRAL_APROBACION = 70;

    private final IPostulanteRepository postulanteRepository;
    private final IPreguntaRepository preguntaRepository;

    public EvaluacionServiceImpl(IPostulanteRepository postulanteRepository,
                                 IPreguntaRepository preguntaRepository) {
        this.postulanteRepository = postulanteRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public EvaluacionDTO calificar(EvaluacionRequest request) {
        if (request == null || isBlank(request.getPostulanteId())) {
            throw new BusinessException("postulanteId requerido");
        }
        Postulante p = postulanteRepository.findById(request.getPostulanteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Postulante no encontrado: " + request.getPostulanteId()));

        // Evitar repetir evaluación
        if (p.getRespuestas() != null && !p.getRespuestas().isEmpty()) {
            throw new BusinessException("El postulante ya tiene una evaluación registrada");
        }
        // Solo se puede evaluar a quien está POSTULADO o EN_EVALUACION
        if (p.getEstado() != EstadoPostulante.POSTULADO
                && p.getEstado() != EstadoPostulante.EN_EVALUACION) {
            throw new BusinessException(
                    "El postulante no está en una etapa que permita evaluar (estado actual: "
                            + p.getEstado() + ")");
        }

        List<Pregunta> preguntas = preguntaRepository.findByOfertaId(p.getOfertaId());
        Map<String, Integer> respuestas = request.getRespuestas() != null
                ? request.getRespuestas()
                : new HashMap<>();

        int total = preguntas.size();
        int correctas = 0;
        for (Pregunta q : preguntas) {
            Integer marcada = respuestas.get(q.getId());
            if (marcada != null && marcada == q.getCorrecta()) correctas++;
        }
        int puntaje = total == 0 ? 0 : (int) Math.round((correctas * 100.0) / total);

        p.setPuntaje(puntaje);
        p.setRespuestas(new HashMap<>(respuestas));
        p.setEstado(puntaje >= UMBRAL_APROBACION
                ? EstadoPostulante.APROBADO_TECNICO
                : EstadoPostulante.EN_EVALUACION);
        postulanteRepository.save(p);

        return new EvaluacionDTO(
                p.getId(), p.getOfertaId(), puntaje, correctas, total,
                respuestas, System.currentTimeMillis());
    }

    @Override
    public EvaluacionDTO obtenerPorPostulante(String postulanteId) {
        Postulante p = postulanteRepository.findById(postulanteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Postulante no encontrado: " + postulanteId));
        if (p.getRespuestas() == null || p.getRespuestas().isEmpty()) {
            throw new ResourceNotFoundException(
                    "El postulante no ha realizado una evaluación todavía");
        }
        List<Pregunta> preguntas = preguntaRepository.findByOfertaId(p.getOfertaId());
        int correctas = 0;
        for (Pregunta q : preguntas) {
            Integer marcada = p.getRespuestas().get(q.getId());
            if (marcada != null && marcada == q.getCorrecta()) correctas++;
        }
        return new EvaluacionDTO(
                p.getId(), p.getOfertaId(), p.getPuntaje(), correctas, preguntas.size(),
                p.getRespuestas(), 0L);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
