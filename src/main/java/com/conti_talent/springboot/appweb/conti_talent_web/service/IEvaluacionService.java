package com.conti_talent.springboot.appweb.conti_talent_web.service;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.EvaluacionDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.dto.request.EvaluacionRequest;

public interface IEvaluacionService {

    /**
     * Califica las respuestas, persiste el resultado en el postulante
     * y mueve el estado: si puntaje >= 70 → APROBADO_TECNICO; sino → EN_EVALUACION.
     * Lanza BusinessException si el postulante ya tiene una evaluación registrada.
     */
    EvaluacionDTO calificar(EvaluacionRequest request);

    /** Recupera la evaluación previamente realizada por un postulante. */
    EvaluacionDTO obtenerPorPostulante(String postulanteId);
}
