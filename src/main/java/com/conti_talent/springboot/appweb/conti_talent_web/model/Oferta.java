package com.conti_talent.springboot.appweb.conti_talent_web.model;

public class Oferta {

    private Long id;
    private String titulo;
    private String descripcion;
    private Long areaId;

    public Oferta() {}

    public Oferta(Long id, String titulo, String descripcion, Long areaId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.areaId = areaId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getAreaId() { return areaId; }
    public void setAreaId(Long areaId) { this.areaId = areaId; }
}
