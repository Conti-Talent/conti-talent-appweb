package com.conti_talent.springboot.appweb.conti_talent_web.dto.response;

public class RankingItemDTO {

    private int posicion;
    private Long postulanteId;
    private String nombre;
    private Long ofertaId;
    private String ofertaTitulo;
    private String estadoCodigo;
    private int puntaje;

    public RankingItemDTO() {
    }

    public RankingItemDTO(int posicion, Long postulanteId, String nombre,
                          Long ofertaId, String ofertaTitulo,
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

    public Long getPostulanteId() { return postulanteId; }
    public void setPostulanteId(Long postulanteId) { this.postulanteId = postulanteId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getOfertaId() { return ofertaId; }
    public void setOfertaId(Long ofertaId) { this.ofertaId = ofertaId; }

    public String getOfertaTitulo() { return ofertaTitulo; }
    public void setOfertaTitulo(String ofertaTitulo) { this.ofertaTitulo = ofertaTitulo; }

    public String getEstadoCodigo() { return estadoCodigo; }
    public void setEstadoCodigo(String estadoCodigo) { this.estadoCodigo = estadoCodigo; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
}
