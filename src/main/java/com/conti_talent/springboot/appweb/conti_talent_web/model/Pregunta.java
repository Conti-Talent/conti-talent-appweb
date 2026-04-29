package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Pregunta del cuestionario técnico asociada a una oferta.
 * `correcta` es el índice (0-based) dentro de `opciones`.
 */
public class Pregunta {

    private String id;
    private String ofertaId;
    private String pregunta;
    private List<String> opciones;
    private int correcta;

    public Pregunta() {
        this.opciones = new ArrayList<>();
    }

    public Pregunta(String id, String ofertaId, String pregunta, List<String> opciones, int correcta) {
        this.id = id;
        this.ofertaId = ofertaId;
        this.pregunta = pregunta;
        this.opciones = opciones != null ? new ArrayList<>(opciones) : new ArrayList<>();
        this.correcta = correcta;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOfertaId() { return ofertaId; }
    public void setOfertaId(String ofertaId) { this.ofertaId = ofertaId; }

    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }

    public List<String> getOpciones() { return opciones; }
    public void setOpciones(List<String> opciones) {
        this.opciones = opciones != null ? new ArrayList<>(opciones) : new ArrayList<>();
    }

    public int getCorrecta() { return correcta; }
    public void setCorrecta(int correcta) { this.correcta = correcta; }
}
