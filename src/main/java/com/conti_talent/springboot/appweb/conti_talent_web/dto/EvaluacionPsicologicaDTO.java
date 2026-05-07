package com.conti_talent.springboot.appweb.conti_talent_web.dto;

public class EvaluacionPsicologicaDTO {
    private Long id;
    private Long postulanteId;
    private long fechaEvaluacion;
    private String resultado;
    private String observacion;
    private String usuarioAdmin;
    private long creadoEn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostulanteId() { return postulanteId; }
    public void setPostulanteId(Long postulanteId) { this.postulanteId = postulanteId; }

    public long getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(long fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getUsuarioAdmin() { return usuarioAdmin; }
    public void setUsuarioAdmin(String usuarioAdmin) { this.usuarioAdmin = usuarioAdmin; }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
