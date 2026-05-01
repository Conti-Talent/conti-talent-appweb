package com.conti_talent.springboot.appweb.conti_talent_web.dto;

import java.util.HashMap;
import java.util.Map;

public class PostulanteDTO {

    private Long id;
    private Long usuarioId;
    private Long ofertaId;
    private String nombre;
    private String email;
    private String telefono;
    private String experiencia;
    private String habilidades;
    private String cv;
    private Long estadoId;
    /** Codigo del estado en mayusculas: POSTULADO, EN_EVALUACION, etc. */
    private String estado;
    private EstadoDTO estadoDetalle;
    private int puntaje;
    private Map<Long, Integer> respuestas;
    private long creadoEn;

    public PostulanteDTO() {
        this.respuestas = new HashMap<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getOfertaId() { return ofertaId; }
    public void setOfertaId(Long ofertaId) { this.ofertaId = ofertaId; }

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

    public Long getEstadoId() { return estadoId; }
    public void setEstadoId(Long estadoId) { this.estadoId = estadoId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public EstadoDTO getEstadoDetalle() { return estadoDetalle; }
    public void setEstadoDetalle(EstadoDTO estadoDetalle) { this.estadoDetalle = estadoDetalle; }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }

    public Map<Long, Integer> getRespuestas() { return respuestas; }
    public void setRespuestas(Map<Long, Integer> respuestas) {
        this.respuestas = respuestas != null ? new HashMap<>(respuestas) : new HashMap<>();
    }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
