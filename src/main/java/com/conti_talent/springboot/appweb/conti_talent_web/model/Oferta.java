package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Oferta laboral o de practicas. Tabla TBL_OFERTA.
 * Tipo y modalidad se guardan como String para conservar 1:1 los valores
 * que ya consume el frontend ('Practica', 'Trabajo', 'Presencial', etc.).
 *
 * `requisitos` y `beneficios` se persisten como tablas auxiliares
 * (TBL_OFERTA_REQUISITO / TBL_OFERTA_BENEFICIO) via @ElementCollection.
 */
@Entity
@Table(name = "tbl_oferta")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 120)
    private String titulo;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "area_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_oferta_area"))
    private Area area;

    @Column(name = "modalidad", length = 20)
    private String modalidad;

    @Column(name = "ubicacion", length = 80)
    private String ubicacion;

    @Column(name = "vacantes", nullable = false)
    private int vacantes;

    @Column(name = "destacada", nullable = false)
    private boolean destacada;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_oferta_requisito",
            joinColumns = @JoinColumn(name = "oferta_id",
                    foreignKey = @ForeignKey(name = "fk_requisito_oferta")))
    @OrderColumn(name = "orden")
    @Column(name = "texto", length = 255, nullable = false)
    private List<String> requisitos;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_oferta_beneficio",
            joinColumns = @JoinColumn(name = "oferta_id",
                    foreignKey = @ForeignKey(name = "fk_beneficio_oferta")))
    @OrderColumn(name = "orden")
    @Column(name = "texto", length = 255, nullable = false)
    private List<String> beneficios;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_oferta_habilidad",
            joinColumns = @JoinColumn(name = "oferta_id",
                    foreignKey = @ForeignKey(name = "fk_habilidad_oferta")))
    @OrderColumn(name = "orden")
    @Column(name = "habilidad", length = 120, nullable = false)
    private List<String> habilidadesRequeridas;

    @Column(name = "creada_en", nullable = false)
    private long creadaEn;

    public Oferta() {
        this.requisitos = new ArrayList<>();
        this.beneficios = new ArrayList<>();
        this.habilidadesRequeridas = new ArrayList<>();
    }

    public Oferta(String titulo, String tipo, Area area, String modalidad,
                  String ubicacion, int vacantes, boolean destacada, String descripcion,
                  List<String> requisitos, List<String> beneficios, long creadaEn) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.area = area;
        this.modalidad = modalidad;
        this.ubicacion = ubicacion;
        this.vacantes = vacantes;
        this.destacada = destacada;
        this.descripcion = descripcion;
        this.requisitos = requisitos != null ? new ArrayList<>(requisitos) : new ArrayList<>();
        this.beneficios = beneficios != null ? new ArrayList<>(beneficios) : new ArrayList<>();
        this.habilidadesRequeridas = new ArrayList<>();
        this.creadaEn = creadaEn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }

    /** Atajo para servicios que solo necesitan el id. */
    public Long getAreaId() { return area != null ? area.getId() : null; }

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

    public List<String> getHabilidadesRequeridas() { return habilidadesRequeridas; }
    public void setHabilidadesRequeridas(List<String> habilidadesRequeridas) {
        this.habilidadesRequeridas = habilidadesRequeridas != null ? new ArrayList<>(habilidadesRequeridas) : new ArrayList<>();
    }

    public long getCreadaEn() { return creadaEn; }
    public void setCreadaEn(long creadaEn) { this.creadaEn = creadaEn; }
}
