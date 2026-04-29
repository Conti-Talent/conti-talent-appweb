package com.conti_talent.springboot.appweb.conti_talent_web.dto.request;

/**
 * Cuerpo del POST /api/postulantes — datos que envía el formulario de postulación.
 */
public class PostularRequest {

    private String usuarioId;
    private String ofertaId;
    private String nombre;
    private String email;
    private String telefono;
    private String experiencia;
    private String habilidades;
    private String cv;

    public PostularRequest() {
    }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getOfertaId() { return ofertaId; }
    public void setOfertaId(String ofertaId) { this.ofertaId = ofertaId; }

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
}
