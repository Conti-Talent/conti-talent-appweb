package com.conti_talent.springboot.appweb.conti_talent_web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Oferta laboral o de prácticas. Mantenemos `tipo` y `modalidad` como String
 * para conservar 1:1 los valores que ya consume el frontend ('Práctica',
 * 'Trabajo', 'Presencial', 'Híbrido', etc.).
 */
public class Oferta {

    private String id;
    private String titulo;
    private String tipo;
    private String areaId;
    private String modalidad;
    private String ubicacion;
    private int vacantes;
    private boolean destacada;
    private String descripcion;
    private List<String> requisitos;
    private List<String> beneficios;
    private long creadaEn;

    public Oferta() {
        this.requisitos = new ArrayList<>();
        this.beneficios = new ArrayList<>();
    }

    public Oferta(String id, String titulo, String tipo, String areaId, String modalidad,
                  String ubicacion, int vacantes, boolean destacada, String descripcion,
                  List<String> requisitos, List<String> beneficios, long creadaEn) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.areaId = areaId;
        this.modalidad = modalidad;
        this.ubicacion = ubicacion;
        this.vacantes = vacantes;
        this.destacada = destacada;
        this.descripcion = descripcion;
        this.requisitos = requisitos != null ? new ArrayList<>(requisitos) : new ArrayList<>();
        this.beneficios = beneficios != null ? new ArrayList<>(beneficios) : new ArrayList<>();
        this.creadaEn = creadaEn;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public int getVacantes() { return vacantes; }
    public void setVacantes(int vacantes) { this.vacantes = vacantes; }

    public boolean isDestacada() { return destacada; }
    public void setDestacada(boolean destacada) { this.destacada = destacada; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getRequisitos() { return requisitos; }
    public void setRequisitos(List<String> requisitos) {
        this.requisitos = requisitos != null ? new ArrayList<>(requisitos) : new ArrayList<>();
    }

    public List<String> getBeneficios() { return beneficios; }
    public void setBeneficios(List<String> beneficios) {
        this.beneficios = beneficios != null ? new ArrayList<>(beneficios) : new ArrayList<>();
    }

    public long getCreadaEn() { return creadaEn; }
    public void setCreadaEn(long creadaEn) { this.creadaEn = creadaEn; }
}
