package com.conti_talent.springboot.appweb.conti_talent_web.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_entrevista_postulante")
public class EntrevistaPostulante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "postulante_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_entrevista_postulante"))
    private Postulante postulante;

    @Column(name = "fecha_entrevista", nullable = false)
    private long fechaEntrevista;

    @Column(name = "resultado", length = 40)
    private String resultado;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "usuario_admin", length = 120)
    private String usuarioAdmin;

    @Column(name = "creado_en", nullable = false)
    private long creadoEn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Postulante getPostulante() { return postulante; }
    public void setPostulante(Postulante postulante) { this.postulante = postulante; }

    public Long getPostulanteId() { return postulante != null ? postulante.getId() : null; }

    public long getFechaEntrevista() { return fechaEntrevista; }
    public void setFechaEntrevista(long fechaEntrevista) { this.fechaEntrevista = fechaEntrevista; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getUsuarioAdmin() { return usuarioAdmin; }
    public void setUsuarioAdmin(String usuarioAdmin) { this.usuarioAdmin = usuarioAdmin; }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
