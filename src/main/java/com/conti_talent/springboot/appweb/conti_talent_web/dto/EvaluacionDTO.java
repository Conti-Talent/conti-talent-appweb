package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * Resultado de una evaluación técnica retornado al postulante:
 * incluye puntaje, número de aciertos y la oferta evaluada.
 */
public class EvaluacionDTO {

    private String postulanteId;
    private String ofertaId;
    private int puntaje;
    private int correctas;
    private int total;
    private Map<String, Integer> respuestas;
    private long evaluadoEn;

    public EvaluacionDTO() {
        this.respuestas = new HashMap<>();
    }

    public EvaluacionDTO(String postulanteId, String ofertaId, int puntaje,
                         int correctas, int total, Map<String, Integer> respuestas, long evaluadoEn) {
        this.postulanteId = postulanteId;
        this.ofertaId = ofertaId;
        this.puntaje = puntaje;
        this.correctas = correctas;
        this.total = total;
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
        this.evaluadoEn = evaluadoEn;
    }

    public String getPostulanteId() { return postulanteId; }
    public void setPostulanteId(String postulanteId) { this.postulanteId = postulanteId; }

    public String getOfertaId() { return ofertaId; }
    public void setOfertaId(String ofertaId) { this.ofertaId = ofertaId; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public int getCorrectas() { return correctas; }
    public void setCorrectas(int correctas) { this.correctas = correctas; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public Map<String, Integer> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<String, Integer> respuestas) {
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
    }

    public long getEvaluadoEn() { return evaluadoEn; }
    public void setEvaluadoEn(long evaluadoEn) { this.evaluadoEn = evaluadoEn; }
}
