package com.conti_talent.springboot.appweb.conti_talent_web.dto;

/**
 * DTO de Rol para exposicion via API REST y paneles administrativos.
 */
public class RolDTO {

    private String id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private long creadoEn;

    public RolDTO() {
    }

    public RolDTO(String id, String codigo, String nombre, String descripcion,
                  boolean activo, long creadoEn) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public long getCreadoEn() { return creadoEn; }
    public void setCreadoEn(long creadoEn) { this.creadoEn = creadoEn; }
}
