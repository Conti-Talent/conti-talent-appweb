package com.conti_talent.springboot.appweb.conti_talent_web.dto.response;

import com.conti_talent.springboot.appweb.conti_talent_web.model.enums.EstadoPostulante;

/**
 * Fila del ranking de postulantes (usada por /api/metricas/ranking).
 * Aplana los datos del postulante para que el frontend pueda renderear sin joins.
 */
public class RankingItemDTO {

    private int posicion;
    private String postulanteId;
    private String nombre;
    private String ofertaId;
    private String ofertaTitulo;
    private EstadoPostulante estado;
    private int puntaje;

    public RankingItemDTO() {
    }

    public RankingItemDTO(int posicion, String postulanteId, String nombre,
                          String ofertaId, String ofertaTitulo,
                          EstadoPostulante estado, int puntaje) {
        this.posicion = posicion;
        this.postulanteId = postulanteId;
        this.nombre = nombre;
        this.ofertaId = ofertaId;
        this.ofertaTitulo = ofertaTitulo;
        this.estado = estado;
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

    public EstadoPostulante getEstado() { return estado; }
    public void setEstado(EstadoPostulante estado) { this.estado = estado; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
}
