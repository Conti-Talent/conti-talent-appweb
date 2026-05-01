package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Postulante a una oferta. Tabla TBL_POSTULANTE.
 * Relaciones: ManyToOne a Usuario (nullable), Oferta y Estado.
 * Las respuestas a la evaluacion se persisten en tabla auxiliar
 * TBL_POSTULANTE_RESPUESTA como Map<preguntaId, indiceElegido>.
 */
@Entity
@Table(name = "tbl_postulante")
public class Postulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_postulante_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_postulante_oferta"))
    private Oferta oferta;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "estado_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_postulante_estado"))
    private Estado estado;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "experiencia", columnDefinition = "TEXT")
    private String experiencia;

    @Column(name = "habilidades", columnDefinition = "TEXT")
    private String habilidades;

    @Column(name = "cv", length = 255)
    private String cv;

    @Column(name = "puntaje", nullable = false)
    private int puntaje;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_postulante_respuesta",
            joinColumns = @JoinColumn(name = "postulante_id",
                    foreignKey = @ForeignKey(name = "fk_respuesta_postulante")))
    @MapKeyColumn(name = "pregunta_id")
    @Column(name = "opcion_elegida", nullable = false)
    private Map<Long, Integer> respuestas;

    @Column(name = "creado_en", nullable = false)
    private long creadoEn;

    public Postulante() {
        this.respuestas = new HashMap<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Long getUsuarioId() { return usuario != null ? usuario.getId() : null; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public Long getOfertaId() { return oferta != null ? oferta.getId() : null; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public Long getEstadoId() { return estado != null ? estado.getId() : null; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getHabilidades() { return habilidades; }
    public void setHabilidades(String habilidades) { this.habilidades = habilidades; }

    public String getCv() { return cv; }
    public void setCv(String cv) { this.cv = cv; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public Map<Long, Integer> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<Long, Integer> respuestas) {
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
    }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
