package com.conti_talent.springboot.appweb.conti_talent_web.dto.response;

/**
 * Fila del ranking de postulantes. Aplana los datos para evitar joins en el
 * cliente. El estado se expone como codigo en mayusculas (POSTULADO,
 * EN_EVALUACION, etc.) para mantener compatibilidad con el JS del front.
 */
public class RankingItemDTO {

    private int posicion;
    private String postulanteId;
    private String nombre;
    private String ofertaId;
    private String ofertaTitulo;
    private String estadoCodigo;
    private int puntaje;

    public RankingItemDTO() {
    }

    public RankingItemDTO(int posicion, String postulanteId, String nombre,
                          String ofertaId, String ofertaTitulo,
                          String estadoCodigo, int puntaje) {
        this.posicion = posicion;
        this.postulanteId = postulanteId;
        this.nombre = nombre;
        this.ofertaId = ofertaId;
        this.ofertaTitulo = ofertaTitulo;
        this.estadoCodigo = estadoCodigo;
        this.puntaje = puntaje;
    }

    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { this.posicion = posicion; }

    public String getPostulanteId() { return postulanteId; }
    public void setPostulanteId(String postulanteId) { this.postulanteId = postulanteId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getOfertaId() { return ofertaId; }
    public void setOfertaId(String ofertaId) { this.ofertaId = ofertaId; }

    public String getOfertaTitulo() { return ofertaTitulo; }
    public void setOfertaTitulo(String ofertaTitulo) { this.ofertaTitulo = ofertaTitulo; }

    public String getEstadoCodigo() { return estadoCodigo; }
    public void setEstadoCodigo(String estadoCodigo) { this.estadoCodigo = estadoCodigo; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
}
