package com.conti_talent.springboot.appweb.conti_talent_web.model;

/**
 * Respuesta marcada por un postulante a una pregunta concreta.
 * Aunque internamente se almacena como Map dentro de Postulante,
 * exponemos esta clase para casos donde se quiera trabajar con la
 * respuesta como entidad independiente (p. ej. histórico).
 */
public class Respuesta {

    private String preguntaId;
    private int opcionElegida;
    private boolean correcta;

    public Respuesta() {
    }

    public Respuesta(String preguntaId, int opcionElegida, boolean correcta) {
        this.preguntaId = preguntaId;
        this.opcionElegida = opcionElegida;
        this.correcta = correcta;
    }

    public String getPreguntaId() { return preguntaId; }
    public void setPreguntaId(String preguntaId) { this.preguntaId = preguntaId; }

    public int getOpcionElegida() { return opcionElegida; }
    public void setOpcionElegida(int opcionElegida) { this.opcionElegida = opcionElegida; }

    public boolean isCorrecta() { return correcta; }
    public void setCorrecta(boolean correcta) { this.correcta = correcta; }
}
