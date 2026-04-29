package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado consolidado de una evaluación técnica.
 * Se usa como objeto de dominio para encapsular la lógica de calificación
 * antes de persistirla en el Postulante.
 */
public class Evaluacion {

    private String postulanteId;
    private String ofertaId;
    private int puntaje;
    private int correctas;
    private int total;
    private List<Respuesta> respuestas;
    private long evaluadoEn;

    public Evaluacion() {
        this.respuestas = new ArrayList<>();
    }

    public Evaluacion(String postulanteId, String ofertaId, int puntaje,
                      int correctas, int total, List<Respuesta> respuestas, long evaluadoEn) {
        this.postulanteId = postulanteId;
        this.ofertaId = ofertaId;
        this.puntaje = puntaje;
        this.correctas = correctas;
        this.total = total;
        this.respuestas = respuestas != null ? new ArrayList<>(respuestas) : new ArrayList<>();
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

    public List<Respuesta> getRespuestas() { return respuestas; }
    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas != null ? new ArrayList<>(respuestas) : new ArrayList<>();
    }

    public long getEvaluadoEn() { return evaluadoEn; }
    public void setEvaluadoEn(long evaluadoEn) { this.evaluadoEn = evaluadoEn; }
}
