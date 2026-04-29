package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Cuerpo del POST /api/evaluaciones — el front envía las respuestas
 * del postulante (preguntaId -> índice elegido).
 */
public class EvaluacionRequest {

    private String postulanteId;
    /** preguntaId -> índice elegido */
    private Map<String, Integer> respuestas;

    public EvaluacionRequest() {
        this.respuestas = new HashMap<>();
    }

    public String getPostulanteId() { return postulanteId; }
    public void setPostulanteId(String postulanteId) { this.postulanteId = postulanteId; }

    public Map<String, Integer> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<String, Integer> respuestas) {
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
    }
}
