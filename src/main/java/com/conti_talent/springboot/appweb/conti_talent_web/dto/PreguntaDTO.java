package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de pregunta para exposición. En endpoints públicos (al postular)
 * se debería filtrar el campo `correcta` antes de retornarlo. Para uso
 * administrativo se devuelve completo.
 */
public class PreguntaDTO {

    private String id;
    private String ofertaId;
    private String pregunta;
    private List<String> opciones;
    private Integer correcta; // Integer para poder anularlo en respuestas públicas

    public PreguntaDTO() {
        this.opciones = new ArrayList<>();
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

    public Integer getCorrecta() { return correcta; }
    public void setCorrecta(Integer correcta) { this.correcta = correcta; }
}
